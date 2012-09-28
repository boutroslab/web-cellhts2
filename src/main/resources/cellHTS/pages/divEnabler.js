/*
 * //
 * // Copyright (C) 2009 Boutros-Labs(German cancer research center) b110-it@dkfz.de
 * //
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU General Public License as published by
 * //    the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //
 * //    You should have received a copy of the GNU General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

jQuery(document).ready(function() {	

    var divEnable = "";
    var divDisableJSON = "";
    var ajaxURI = jQuery("#ajax_uri").text();

    receiveAjaxDataFromServer(ajaxURI);


});

function setNewPageDetails(divEnable,divDisableList) {
    
    if (divDisableList == null) {
        alert("something messed up regarding your DIV list you want to disable");
    }
    if (divEnable == null) {
        alert("failed to provide proper DIV element you want to enable");
    }

    //first make all divs invisible
    var i;
    for (var i=0; i<divDisableList.length; i++) {
        var elementName = divDisableList[i];
        if (elementName == divEnable) {
            continue;
        }
        var element = jQuery("#"+elementName);
        if (element[0] ) {
            element.hide();
            //     alert("debug setting element to none display"+elementName);
        }
        else {
            alert("element " + elementName + " is nonexistand in html");
        }
    }
    //now make the one visible you want to show
    var enableElement = jQuery("#"+divEnable);
    if (enableElement[0]) {
        enableElement.show();
        // alert("debug setting element to enable display"+divEnable);
    }
    else {
        alert("element " + divEnable + " is nonexistand in html");
    }
}

function receiveAjaxDataFromServer(ajaxURI) {
    new Ajax.Request(
            ajaxURI,
    {
        method: 'post',
        //return the complete JSON string of all enabled wells..the logic lies in java
        onSuccess: function(response) {
            var jSONText = response.responseText;
            var jSONStuff = jSONText.evalJSON();
            var divEnable = jSONStuff['divEnable'];
            var divDisable = jSONStuff['divDisable'];
            setNewPageDetails(divEnable,divDisable);
        }
    }
            );
}


