xquery version "3.0";
 
module namespace sec-ev="http://exist-db.org/security/events";
 
declare function sec-ev:authentication() {
    util:log-system-out(concat("An authentication event has occurred for ", 
xmldb:get-current-user()))
};
