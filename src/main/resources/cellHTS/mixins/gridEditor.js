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

var GridEditor = Class.create();
GridEditor.prototype = {
    //this class enables inplace edition for all the elements of
    //a table--> it was written to work with the Grid Component
    //it uses the InPlaceEditor from scriptaculos so you have
    //to include it in your java code

    //this javascript was tested only with firefox 3




    initialize: function(divElement, URI, excludeColumns, paramName) {


        //go from the surrounding div element directly to the table element
        var tableElement = document.getElementById(divElement).firstChild.firstChild;
        this.paramName=paramName;
        //if we dropped the table
        if (tableElement == null) {
            return;
        }
        //traverse to all childs with the name tr which are connected with our table element
        this.rows = tableElement.getElementsByTagName("tr");
        this.URI = URI;
        //the excluded columns are 1 based not typical array-zero-based
        this.excludColumns = excludeColumns;
        //set a class variable which all instances can access
        GridEditor.prototype.URI = URI;

        //we must keep the object reference to make it use in event callback methods later
        //we have to store the object reference from this grideditor instance for later usage
        // to keep access to it in event callback function
        GridEditor.prototype.object = this;

        //store if we have already sent an ajax request in this round
        GridEditor.prototype.wrongFormat = false;

        GridEditor.prototype.currentEditfield = null;

        GridEditor.prototype.createNewTextField = false;


        //if we have an empty table or one only consisting of header line
        if (this.rows.length < 2) {
            return;
        }
        this.colLength = this.rows[0].cells.length;
        this.rowLength = this.rows.length;
        var i = 0;
        //binds important data cells from the grid to mouse click events
        this.bindCellsMouseClickEvent();


        //TODO: alert method gets called twice and this will kill everything!!!

    },
    bindCellsMouseClickEvent : function() {

        //translate all columns you want to exclude from here
        var excludeColumnArr = this.excludColumns.split(",");
        //make an hash out of the arry
        var assocArr = new Array();
        for (var i = 0; i < excludeColumnArr.length; i++) {
            if (excludeColumnArr[i]) {
                //minus 1 because this parameter is one based
                assocArr[excludeColumnArr[i] - 1] = 1;
            }
        }


        //start at second row
        for (var i = 1; i < this.rowLength; i++) {
            var rowOI = this.rows[i];
            var cells = rowOI.cells;
            for (var j = 0; j < this.colLength; j++) {
                //if not an excluded column ...we will register it
                if (!assocArr[j]) {


                    //for every element
                    var element = cells[j];

                    //we define an element id (row_column) because otherwise we will lose this information
                    element.id = i + "_" + j;
                    //attach this cell to an event handler
                    //Event.observe(element, 'click', this.cellClickEvent.bindAsEventListener(this));
                    this.catchEvent(element,"click",this.cellClickEvent);
                    //we cannot use Event.observe for blur and keypress because they do not work properly in Mozilla >=3
                    //element.addEventListener("blur", this.focusChange, true);
                    //this.catchEvent(element,"change",this.focusChange,true);
                    //element.addEventListener("keypress", this.returnKeyPressed, false);
                    this.catchEvent(element,"keypress",this.returnKeyPressed);


                }

            }
        }
    },
    //this method can send Ajax from HTML elements to a specified URI
    sendViaAjax: function (id, obj) {
        var msg = GridEditor.prototype.currentEditfield.value;
        var expression = /\D/;
        var thisObj = GridEditor.prototype.object;
        var element = thisObj.clickedElementObj;
        var filename =thisObj.getInnerText(element.parentNode.firstChild);
        if(filename==null) {
            alert("fatal error occured while trying to receive filename");
        }
        //check if we have non digits TODO: if i want to release this
        //mixin i have to extend this to a parameter which defines allowed
        //datatypes for every column....
        if (expression.test(msg)) {
            thisObj.setInnerText(element,thisObj.clickedElementValue);
            //element.textContent = thisObj.clickedElementValue;
            GridEditor.prototype.wrongFormat = true;
            alert("cannot process non-numbers");
        }
        else {
            GridEditor.prototype.wrongFormat = false;
            var URI = GridEditor.prototype.URI;
            var arr = id.split("_");
            var data = { row : arr[0],   //row
                column :arr[1],          //column
                value : msg,              //value
                filename : filename
            };

            var jsonObj = Object.toJSON(data);
            var encodedJsonObj = encodeURIComponent(jsonObj);
            //var encoding = this.getCharacterEncoding();

            new Ajax.Request(
                    URI,
            {
                //send the JSON with a parameter name which can be interpreted from tapestrys request obj later
                parameters: this.paramName+"="+encodedJsonObj,
                onSuccess: function(response) {
                }
            }
                    );
        }
    },
    returnKeyPressed: function(evt) {

        //everytime we hit a key we must be in a textfield therefore
        //we cant have just created it
        //this is necessary because IE8 blurs when creating a textfield element
        GridEditor.prototype.createNewTextField = false;

        //IE 8 uses window.event to access possible events
        if (!evt) var evt = window.event;
        var thisObj =GridEditor.prototype.object;

        thisObj.clickEvent = false;

        var code;
        if (evt.keyCode) code = evt.keyCode;
        else if (evt.which) code = evt.which;
        //this is for debugging
        var key = String.fromCharCode(code);


        var pressedKey = code;
        
        //we have to set on current bubbling to use this
        var editField = GridEditor.prototype.currentEditfield;
        var oldValue = thisObj.clickedElementValue;
        var element = thisObj.clickedElementObj;
        //when were running the first time this will be empty because we innerHTML="" in cellClickEvent!!
        var id = thisObj.clickedElementID;

        if (thisObj.getInnerText(element) == null) {
            //set the textcontent to zero if non existant
            thisObj.setInnerText(element, "");
            return;
        }
        //if key pressed is tab or return send event
        if (pressedKey == 9 || pressedKey == 13) {

            //do not sent if nothing changed
            //do only make an AJAX request if the "new" text differs from the old one

            //the editField.value=="" is special: if we have an empty editfield for any reason te parent editfield is empty too
            //so that we cannot see if the oldvalue is the same as the new value

            //erase all whitespaces
            editField.value=editField.value.replace(/\s/, '');
            //alert(">"+editField.value+"<");

            if ((editField.value != oldValue || editField.value == "")) {

                //we have to call sendViaAjax from the grid editor obj to keep contact to it
                GridEditor.prototype.object.sendViaAjax(id, editField);


            }
            //only update it if we havent got any error
            if (!GridEditor.prototype.wrongFormat) {
//                element.textContent = editField.value;
                thisObj.setInnerText(element,editField.value);
            }
        }

    },
    focusChange : function(event) {
        //IE8 specific
        //this is necessary because IE8 blurs when creating a textfield element
        //but we dont want to get in here if we are creating a textfield
        if(GridEditor.prototype.createNewTextField) {
            GridEditor.prototype.createNewTextField = false;
            
            var thisObj = GridEditor.prototype.object;
            var element = thisObj.clickedElementObj;
            thisObj.setInnerText(element,thisObj.clickedElementValue);
            return;
        }
        

        //IE 8 uses window.event to access possible events
        if (!event) var event = window.event;

        var editField = GridEditor.prototype.currentEditfield;
        // get the element from which this editField is embedded

        var thisObj = GridEditor.prototype.object;
        var element = thisObj.clickedElementObj;

        var id = thisObj.clickedElementID;
        var oldValue = thisObj.clickedElementValue;

        //do only make an AJAX request if the "new" text differs from the old one
        //the editField.value=="" is special: if we have an empty editfield for any reason te parent editfield is empty too
        //so that we cannot see if the oldvalue is the same as the new value

        //erase all whitespaces
        editField.value=editField.value.replace(/\s/, '');
        //alert(">"+editField.value+"<");

        if (editField.value != oldValue || editField.value == "") {
            GridEditor.prototype.object.sendViaAjax(id, editField);

        }
        if(GridEditor.prototype.wrongFormat) {
            editField.value="";
        }

//            element.textContent = editField.value;
        thisObj.setInnerText(element,editField.value);
        


    },
    cellClickEvent: function(mouseEvent) {
        //IE 8 uses window.event to access possible events
        if (!mouseEvent) var mouseEvent = window.event;
        //IE8 has relatedTarget, Netscape fromElement
        var element = mouseEvent.target ? mouseEvent.target : mouseEvent.srcElement;
        //var element = mouseEvent.currentTarget;
        
        var thisObj =GridEditor.prototype.object;

        if (element.tagName != "TD") return;
        if (element.firstChild && element.firstChild.tagName == "INPUT") return;
        var id = element.id;
        var innerHTML = element.innerHTML;
        //if we have clicked an empty field we have to overwrite
        //otherwise we get a &nbsp; symbol
        if (!innerHTML || innerHTML == "&nbsp;") {
            innerHTML = "";
        }
        //erase this to show the editfield instead
        element.innerHTML = "";

        //store the content of the clicked element
        thisObj.clickedElementValue = innerHTML;
        thisObj.clickedElementID = element.id;
        thisObj.clickedElementObj = element;

        var editField = document.createElement("INPUT");
        editField.type = "text";
        editField.value = "" + innerHTML;

        thisObj.catchEvent(editField,"blur",thisObj.focusChange,true);
        element.appendChild(editField);
        editField.style.width = "100%";

        //mark / select the element
        editField.select();
        //we have to store this in the obj so we can access this info in event handler methds
        GridEditor.prototype.currentEditfield = editField;

        //mark theat we just created/openend a new Textfield
        GridEditor.prototype.createNewTextField = true;
       

    },
    //this is for debugging only
    getCharacterEncoding:function () {
        var charSet = "";
        // list of properties to try read for the current pages encoding.
        // document.characterSet is used by FF 2/3 &amp; Safari
        // document.charset is used by IE 6/7
        // the others are for belt and braces
        var testList = ["document.characterSet",
            "document.charset",
            "document.defaultCharset",
            "document.actualEncoding",
            "document.inputEncoding"
        ];
        for (var i = 0; i < testList.length; i++) {
            try {
                charSet = "notset";
                eval("charSet = " + testList[i] + ";");
                if (charSet != "" && charSet != null) {
                    break;
                }
            } catch(e) {
            }
        }
        return charSet;
    },
    //for cross-browser compatibiltiy regarding event listeners we need to add the following code
    catchEvent : function(eventObj,event,eventHandler) {
        //mozilla and netscape event handling (W3C standard)
        if(eventObj.addEventListener) {
            eventObj.addEventListener(event,eventHandler,false);
        }
        //windows event handling mechanism!!!
        else if (eventObj.attachEvent) {
            event = "on"+event;
            eventObj.attachEvent(event,eventHandler)
        }
    },
    //for cross-browser compatibiltiy regarding event listeners we need to add the following code
    catchEvent : function(eventObj,event,eventHandler,boolVar) {
        //mozilla and netscape event handling (W3C standard)
        if(eventObj.addEventListener) {
            eventObj.addEventListener(event,eventHandler,boolVar);
        }
        //windows event handling mechanism!!!
        else if (eventObj.attachEvent) {
            event = "on"+event;
            eventObj.attachEvent(event,eventHandler)
        }
    },
    //IE8 has got function innerText, Mozilla Firefox has textContent
    setInnerText : function(element,text) {
        if(element.textContent!=null) {
            element.textContent = text;
        }
        else {
            element.innerText=text;
        }
    },
    getInnerText : function(element) {
       if(element.textContent!=null) {
           return element.textContent;
       }
       else {
           return element.innerText;
       }
    }
   

    

}



