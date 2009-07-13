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

var ProgressBar = Class.create();
ProgressBar.prototype = {
    /**
     *
     * original code by:
     *
     * @author Ryan Johnson <http://syntacticx.com/>
     * @copyright 2008 PersonalGrid Corporation <http://personalgrid.com/>
     * @package LivePipe UI
     * @license MIT
     * @url http://livepipe.net/control/progressbar
     * @require prototype.js, livepipe.js
     * modified by o.pelz@dkfz.de
     */

    //this class was improved by o.pelz@dkfz.de to allow progress text
    initialize: function(container, textContainer,options) {

        if (typeof(Prototype) == "undefined")
            throw "ProgressBar requires Prototype to be loaded.";
        if (typeof(Object.Event) == "undefined")
            throw "ProgressBar requires Object.Event to be loaded.";


        this.progress = 0;
        this.progressText="";
        this.executer = false;
        this.active = false;
        this.poller = false;
        this.container = $(container);
        this.textContainer = $(textContainer);
        this.containerWidth = this.container.getDimensions().width - (parseInt(this.container.getStyle('border-right-width').replace(/px/, '')) + parseInt(this.container.getStyle('border-left-width').replace(/px/, '')));
        this.progressContainer = $(document.createElement('div'));
        this.progressContainer.setStyle({
            width: this.containerWidth + 'px',
            height: '100%',
            position: 'absolute',
            top: '0px',
            right: '0px'
        });
        this.container.appendChild(this.progressContainer);
        this.options = {
            afterChange: Prototype.emptyFunction,
            interval: 0.25,
            step: 1,
            classNames: {
                active: 'progress_bar_active',
                inactive: 'progress_bar_inactive'
            }
        };
        Object.extend(this.options, options || {});
        this.container.addClassName(this.options.classNames.inactive);
        this.active = false;
    },
    setProgress: function(value,successUrl) {
        //we cannot simply slit cause there could be more than one underscore in the text e.g. 20_HELLO_WORLD so
        //only take the first one
        var slashPos = value.indexOf("_");
        this.progress = value.substring(0,slashPos);
        this.progress = parseInt(this.progress);
        this.progressText = value.substring(slashPos+1);

        //var arr = value.split("_");
        //this.progress=parseInt(arr[0]);
        //this.progressText = arr[1];


        //a progress bigger than 100 is a error
        if(this.progress>100) {
            this.stop(false);

            this.draw();
        }
        if (this.progress == 100) {
            this.stop(false);
            
            this.draw();
            //if we have 100% we are done and should send call the
            //success method on the server
            //new Ajax.Request(successUrl, { method:'post' });
            //window.open(successUrl,"Results");
            window.location.href=successUrl;


        }
        else {
            this.draw();
            this.notify('afterChange', this.progress, this.active);
        }
    },
    poll: function(pollUrl,successUrl, interval) {
        this.active = true;
        this.poller = new PeriodicalExecuter(function() {
            new Ajax.Request(pollUrl, {
                onSuccess: function(request) {
                   
                    this.setProgress(request.responseText,successUrl);
                    if (!this.active)
                        this.poller.stop();
                }.bind(this)
            });
        }.bind(this), interval || 3);
    },
    start: function() {
        this.active = true;
        this.container.removeClassName(this.options.classNames.inactive);
        this.container.addClassName(this.options.classNames.active);
        this.executer = new PeriodicalExecuter(this.step.bind(this, this.options.step), this.options.interval);
    },
    stop: function(reset) {
        this.active = false;
        if (this.executer)
            this.executer.stop();
        this.container.removeClassName(this.options.classNames.active);
        this.container.addClassName(this.options.classNames.inactive);
        if (typeof(reset) == 'undefined' || reset == true)
            this.reset();
    },
    step: function(amount) {
        this.active = true;
        this.setProgress(Math.min(100, this.progress + amount));
    },
    reset: function() {
        this.active = false;
        this.setProgress(0);
    },
    draw: function() {
        this.textContainer.innerHTML=this.progressText;
        var myProgress= parseInt(this.progress);
        if(myProgress>100) {
            myProgress=100;
        }
        this.progressContainer.setStyle({
            width: (this.containerWidth - Math.floor((myProgress / 100) * this.containerWidth)) + 'px'
        });

    },
    notify: function(event_name) {
        if (this.options[event_name])
            return [this.options[event_name].apply(this.options[event_name], $A(arguments).slice(1))];
    }

};
Object.Event.extend(ProgressBar);