#import "TraxorPlugin.h"
#import <Cordova/CDV.h>
#import <Cordova/NSArray+Comparisons.h>
#import <Cordova/NSDictionary+Extensions.h>
#import "CDVContacts.h"

@implementation TraxorPlugin

- (void) getContactsByIds:(CDVInvokedUrlCommand*) command
{
    
    NSString* callbackId = command.callbackId;
    NSArray* ids = [command.arguments objectAtIndex:0];
    NSArray* fields = [command.arguments objectAtIndex:1 withDefault:[NSNull null]];
  
    [self.commandDelegate runInBackground:^{
        // from Apple:  Important You must ensure that an instance of ABAddressBookRef is used by only one thread.
        // which is why address book is created within the dispatch queue.
        // more details here: http: //blog.byadrian.net/2012/05/05/ios-addressbook-framework-and-gcd/
        CDVAddressBookHelper* abHelper = [[CDVAddressBookHelper alloc] init];
        TraxorPlugin* __weak weakSelf = self;     // play it safe to avoid retain cycles
        // it gets uglier, block within block.....
        [abHelper createAddressBook: ^(ABAddressBookRef addrBook, CDVAddressBookAccessError* errCode) {
           if (addrBook == NULL) {
                // permission was denied or other error - return error
                CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageToErrorObject:errCode ? (int)errCode.errorCode:UNKNOWN_ERROR];
                [weakSelf.commandDelegate sendPluginResult:result callbackId:command.callbackId];
                return;
           }

            // get the findOptions values
            NSDictionary* returnFields = [[CDVContact class] calcReturnFields:fields];

            NSMutableArray* returnContacts = [NSMutableArray arrayWithCapacity:1];
            ABRecordRef rec = nil;
            
            // Casting here since if the user has more than 2^32-1 contacts then we have bigger problems.
            int count = (int) [ids count];
            
            NSNumber* id = nil;
            
            @autoreleasepool {
            
            for (int j = 0; j < count; j++)
            {
                
                id = [ids objectAtIndex:j];
                
                if (id && ![id isKindOfClass:[NSNull class]] && ([id intValue] != kABRecordInvalidID))
                {
                    
                    rec = ABAddressBookGetPersonWithRecordID (addrBook, [id intValue]);
                
                    if (rec)
                    {
                    
                        CDVContact* contact = [[CDVContact alloc] initFromABRecord:rec];
                        
                        if (contact)
                        {
                            
                            NSDictionary* aContact = [contact toDictionary:returnFields];
                            [returnContacts addObject:aContact];

                        }
                  
                    }
                }
            }
                
            } // end autoreleasepool

            // return found contacts (array is empty if no contacts found)
            CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:returnContacts];
            [weakSelf.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            
            if (addrBook) {
                CFRelease (addrBook);
            }
            
        }]; // end of address Book worker block
        
    }]; // end of workQueue block
    
    return;
}

@end
