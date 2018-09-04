var traxor = function ()
{
    
    var isArray = function (o)
    {
        
        if (o === null)
        {
            
            return false;
            
        }
        
        return o.constructor == (new Array).constructor;
        
    };
    
    return {
    
    getContactIds : function (successCB, errorCB)
    {
        
            navigator.contacts.find (
            // Fields to return.
            [
                navigator.contacts.fieldType.id
            ],
            function (objs)
            {
                                    
                                     
                var ids = [];
                
                // Iterate over to strip out the junk (reduce memory).
                for (var i = 0; i < objs.length; i++)
                {
                    
                    ids.push (objs[i].id);
                    
                }
                   
                successCB (ids);
                                 
                
            },
            function (error)
            {
                                     
                                     if (error.code != ContactError.NOT_FOUND_ERROR)
                {
        
                    traxor.utils.logError (null,
                                           'cannot process contacts: ' + error,
                                           error);
                    
                } else {
                    
                    successCB ([]);
                    
                }

            },            
            {
                multiple : true,
                filter : '',
                                     desiredFields : [ navigator.contacts.fieldType.id ]
            });
        
    },

    getContactsByIds : function (ids, successCB, errorCB, fields)
    {
    
        if (!successCB) {
            throw new TypeError("You must specify a success callback for the getByIds command.");
        }
    
        if (!fields || (isArray(fields) && fields.length === 0))
        {
        
            if (typeof errorCB === "function") {
                errorCB(new ContactError(ContactError.INVALID_ARGUMENT_ERROR));
            }
        
        } else {

            var win = function(result) {
                var cs = [];
               
                for (var i = 0, l = result.length; i < l; i++) {
                    cs.push(navigator.contacts.create(result[i]));
                }
                successCB(cs);
            };
            try{
            cordova.exec(win, errorCB, "TraxorPlugin", "getContactsByIds", [ids, fields]);
    }catch(e){console.log(e);}
        }

    },
    
    modelAsString : function (successCB,
                              errorCB)
    {
        
        
        
    }

    };
    
}();

module.exports = traxor;
