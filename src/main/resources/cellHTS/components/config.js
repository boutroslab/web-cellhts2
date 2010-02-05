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

//this javascript file defines configuration parameters for all the javascripts used by this component

//this defines the types of plate colors for the differnet welltypes

//TODO:please note:only color names and no color codes are supported right now...line //make a translation hash for color=>type in plate.js is the culprit because of numbers escpecially leading zeros as hash index
var plateColors={"pos":"red",
                 "neg":"blue",
                 "other":"black",
                 "empty":"white",
                 "sample":"grey",

                 "cont1":"green"
                 
};
//the text colors for the wells
var textColors={"pos":"black",
                 "neg":"white",
                 "other":"white",
                 "empty":"black",
                 "sample":"black",
                 "cont1":"black"



}