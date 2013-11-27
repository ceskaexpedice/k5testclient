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

var pid_path_str = '';
var model_path_str = '';
var pid_path;
var model_path;
var loadingInitNodes;
    
    
function initTree(pps, mps, title, viewable){
    
    
    $("#item_tree li>div>a").live('click', function(event){
        var id = $(this).parent().parent().attr('id');
        nodeClick(id);
        event.preventDefault();
        event.stopPropagation();
    });
    
    $("#item_tree li>span.folder").live('click', function(event){
        $(this).parent().children("ul").toggle();
        $(this).toggleClass("ui-icon-triangle-1-s");
        //nodeToggle();
    });

    $('#item_tree.viewer').bind('viewChanged', function(event, id){
        selectNodeView(id);
    });
    
    pid_path_str = pps;
    model_path_str = mps;
    pid_path = pid_path_str.split('/');
    model_path = model_path_str.split('/');
    
    var current = $("#item_tree");
    
    for(var i = 0; i< initContext.length; i++){
        for(var j = 0; j<initContext[i].length; j++){
            current = addNode(current, initContext[i][j].pid, initContext[i][j].model, initContext[i][j].title, initContext[i][j].viewable);
            getSibling(current, initContext[i][j].pid);
        }
    }

    $('#rightMenuBox').tabs({
        show: function(event, ui){
            var tab = ui.tab.toString().split('#')[1];
            var t = "";
            if (tab=="contextMenu"){
                ctxMenuOpen();
            }else{

            }
        }
    });
}


    var span = '<span class="ui-icon ui-icon-triangle-1-e folder " >folder</span>';
function getSibling(obj, pid){
    var url = "api/item/" + pid + "/siblings";
    $.getJSON(url, function(data) {
        var idx = false;
        $.each(data[0].siblings, function(i, item) {
            if(item.pid != pid){
                var li2 = addSibling(item.pid, item.model, item.title, true);
                if(idx){
                    $(obj).parent().append(li2);
                }else{
                    $(obj).before(li2);
                }
            }else{
                idx = true;
            }
                
        });

    });
}
function addSibling(pid, model, title, viewable){
    
    var li2 = $('<li style="clear:both;" id="' + model + '_' + pid + '"></li>');
    if(viewable){
        $(li2).addClass("viewable");
    }
    $(li2).append(span);
    var div = $('<div style="float:left;"><input type="checkbox"  /></div>');
    $(li2).append(div);
    var div2 = $('<div style="float:left;"><a href="#" class="label">' + title + '</a></div>');
    $(li2).append(div2);
    
    return li2;
    
}

function addNode(node, pid, model, title, viewable){
    
    var nnode = $('<ul></ul>');
    
    $(node).append(nnode);
    
    var li = $('<li style="clear:both;" class="' + model + '"></li>');
    $(nnode).append(li);
    
    $(li).append(span);
    var a = $('<a href="#" class="model">' + dictionary['fedora.model.' + model] + '</a>');
    $(li).append(a);
    var ul = $('<ul></ul>');
    $(li).append(ul);
    var li2 = $('<li style="clear:both;" id="' + model + '_' + pid + '"></li>');
    if(viewable){
        $(li2).addClass("viewable");
    }
    $(ul).append(li2);
    $(li2).append(span);
    var div = $('<div style="float:left;"><input type="checkbox"  /></div>');
    $(li2).append(div);
    var div2 = $('<div style="float:left;"><a href="#" class="label">' + title + '</a></div>');
    $(li2).append(div2);
    
    
    
    return li2;
    
    
    
}



function nodeClick(id){
    
}

function selectNodeView(id){
    
}
