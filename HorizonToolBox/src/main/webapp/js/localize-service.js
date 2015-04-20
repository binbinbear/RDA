angular.module('localization', []).
   factory('localize', ['$http', '$window',
      function ($http, $window) {
         /**
          * Deals with some of the L10N features like using display text based on user's locale.
          */
         var locale = {

           
            // Locale to be used during translation. Default is English.
            locale : 'en',

            // JSON object containing the translated text.
            translatedTable : null,

            // Get Locale info
            getLocale : function() {
               return this.locale;
            },

            // Set the locale and load the corresponding JSON translation table.
            setLocaleAndLoad : function(acceptLanguage) {
               var self = this;
               console.info("window.l10Ntable:"+window.l10Ntable);
               self.translatedTable = window.l10Ntable;
               console.info("window.l10Ntable:"+self.translatedTable);
            },

            translate : function(msgid, args) {
               var translatedStr;

               /**
                * If the translated msgid exists, return the translated string, else
                * return the original string.
                */
        //       console.log("!!this.translatedTable------------"+!!this.translatedTable);
        //       console.log("!!this.translatedTable[msgid]--------"+!!this.translatedTable[msgid]);
               if (!!this.translatedTable && !!this.translatedTable[msgid]) {
                  /**
                   * The tool po2json used for converting po to json puts the translated
                   * string to an array and the translated result is in the second
                   * element.
                   */
                  translatedStr =  this.translatedTable[msgid];
               } else {
                  translatedStr = msgid;
               }

               return translatedStr.replace(/\{(\d+)\}/g, function(match, mNum, offset, fullStr) {
                  var retStr;
                  var intNum = parseInt(mNum, 10);
                  // For escaped case, remove the duplicated "{" and "}".
                  if (fullStr.charAt(offset - 1) === "{" &&
                      fullStr.charAt(offset + match.length) === "}") {
                     return mNum;
                  }
                  if (!!args[intNum + 1]) {
                     retStr = args[intNum + 1];
                  } else {
                     retStr = match;
                  }

                  return retStr;
               });
            },

            

            
         };

//         locale.setLocaleAndLoad(VIEWCLIENT.acceptLanguage);
         locale.setLocaleAndLoad();
         return locale;
      }]).
   filter('i18n', ['localize', function (localize) {
         return function (input) {
            return localize.translate(input, arguments);
         };
      }]);