/* 
 * Copyright (C) 2013 alberto
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

function item(pid) {
    var url = api + "api/item/" + pid + "/context";
    $.getJSON(url, function(data) {

    });
}

function decade(i, y){
    
}

function doDateAxis(data){

            
    var max = maxYear;
    var wt = years.length * 3 + 10;
    for(var i=0; i<years.length; i++){
        var l = years[i];
        var c = years[++i];
        var title = l + " (" + c + ")";
        var d1 = l.toString();
        d1 = substring(d1, 3);
        var h = parseInt(c) / max * 100;
        var m = 100 - h;
        var dagroup = '<div class="da_group" id="da_group_'+d1+'">'+
            '<div class="da_group_title">'+d1+'0 - '+d1+'9</div>' +
            '<div class="da_space"></div>' +
            decade(1, 0) +
            '</div>';
        $("#da_container").append(dagroup);
    }
}

function addFilter(field, value){
    var page = new PageQuery(window.location.search);
    page.setValue("offset", "0");
    page.setValue("forProfile", "facet");

    var f = field + "=\"" + value + "\"";
    if(window.location.search.indexOf(f)==-1){
        window.location = "search.vm?" +
        page.toString() + "&" + f;
    }
}

var rollIndex = 0;
function rollTypes(){
    $('#dt_home>div>span:nth('+rollIndex+')').fadeOut(function(){
        rollIndex++;
        if(rollIndex>=$('#dt_home>div>span').length) rollIndex=0;
        $('#dt_home>div>span:nth('+rollIndex+')').show();
        setTimeout('rollTypes()', 4000);
    });
}
