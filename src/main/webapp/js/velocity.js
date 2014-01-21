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
    
    
    var url = "api/item/" + pid + "/context";
    $.getJSON(url, function(data) {
        var url = data.rss;
        $.each(data.data, function(i, item) {
            var div = $('<div align="center" class="item"></div>');
            var a = $('<a href="item.vm?pid='+item.pid+'" ></a>');
            var img = $('<img align="middle" height="96" border="0" vspace="2" src="api/item/' + item.pid + '/thumb" title="' + item.title + '" alt="' + item.title + '" />');
            $(div).append('<div></div>');
            $(a).append(img);
            $(div).append(a);
            $("#newest").append(div);
        });
        
    });
    
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

function setVirtualCollection(collection){
    var page = new PageQuery(window.location.search);
    page.setValue("collection", collection);
    var url = "?" + page.toString();
    window.location = url;
}

function refreshCollections(){
     $("#vc>div.collections>ul>li").remove();
    var url = "api/vc";
    $.getJSON(url, function(data) {
        $.each(data, function(i, item) {
            var a = $('<li><a href="javascript:setVirtualCollection(\''+item.pid+'\');">'+item[language]+'</a></li>');
            $("#vc>div.collections>ul").append(a);
        });
        
    });
}

function getCollsDict(){
    var url = "api/vc/";
    $.getJSON(url, function(data) {
        $.each(data, function(i, item) {
            collectionsDict[item.pid.toString()] = item[language];
        });
        
        $("div.search_result>div.collections>div.cols>div.collection").each(function(){
            var id = $(this).data("vcid");
            var title = "";
            title = collectionsDict[id];
            $(this).html(title);
        });
        
        $("#filters li.collection>ul>li>a").each(function(){
            var id = $(this).text();
            var title = "";
            title = collectionsDict[id];
            $(this).html(title);
        });
        
        $("#facets li.collection>ul>li>a").each(function(){
            var id = $(this).html();
            var title = "";
            title = collectionsDict[id];
            $(this).html(title);
        });
    });
}
