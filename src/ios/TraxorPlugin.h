#import <Cordova/CDV.h>

@interface TraxorPlugin : CDVPlugin

- (void) getContactsByIds:(CDVInvokedUrlCommand*) command;

@end;