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

var loadingInitNodes;


function initTree(pps, mps, title, viewable) {


    $('#item_tree').css('width', $('#itemTree').width() - 20);
    $('#rightMenuBox>h3').addClass('ui-state-default ui-corner-top ui-tabs-selected ui-state-active ');
    $("#item_tree li>div>a").live('click', function(event) {
        nodeClick(this);
        event.preventDefault();
        event.stopPropagation();
    });

    $("#item_tree li>span.folder").live('click', function(event) {
        nodeToggle($(this).parent());
    });

    $('#item_tree.viewer').bind('viewChanged', function(event, id) {
        selectNodeView(id);
    });


    $('#donator.viewer').bind('viewReady', function(event, viewerOptions) {
        checkDonator(viewerOptions);
    });

    $('#suggest.viewer').bind('viewReady', function(event, viewerOptions) {
        getSuggested(viewerOptions);
    });

    $(window).bind('hashchange', function(e) {
        checkHashChanged(e);
    });


    $('#rightMenuBox').tabs({
        show: function(event, ui) {
            var tab = ui.tab.toString().split('#')[1];
            var t = "";
            if (tab == "contextMenu") {
                ctxMenuOpen();
            } else {

            }
        }
    });

    var current = $("#item_tree");
    

    for (var i = 0; i < initContext.length; i++) {
        var mPath = "";
        for (var j = 0; j < initContext[i].length; j++) {
            //if (initContext[i][j].viewable == "true") {
                //lastPid = initContext[i][j].model_path[0].replaceAll("/", "-") + "_" + initContext[i][j].pid;
                if(j>0) mPath += "-"
                mPath += initContext[i][j];
                lastPid = mPath + "_" + initContext[i][j].pid;
            //}
            var el = initContext[i][j];
            var title = el.title;
            if(el.details && el.details.length > 0){
                title = getTitleByModel(el.details[0], el.model, el.root_title);
            }
            current = addNode(current, el.pid, el.model[0], el.model, title, el.viewable);
            var isLast = (j == (initContext[i].length - 1)) && (i == (initContext.length - 1));
            getSiblings(current, initContext[i][j].pid, initContext[i][j].model, isLast);
        }
    }


    showNode(lastPid);
    setActiveUuids(lastPid);
    initView = true;
    setInitActive();
    $(".viewer").trigger('viewChanged', [lastPid]);
}

function getTitleByModel(details, model, root_title){
    var dArr = details.split("##");
    if(model==="periodicalvolume"){
        return dictionary['Datum vydání'] + ": " + dArr[0] + " " + 
                dictionary['search.details.periodicalvolumenumber.in.tree'] + " " + dArr[1];
    }else if(model==="internalpart"){
        return dArr[0] + " " + dArr[1] + " " + dArr[2] + " " + dArr[3];
    }else if(model==="periodicalitem"){
        if(dArr[0] != root_title){
            return dArr[0] + " " + dArr[1] + " " + dArr[2];
        }else{
            return dArr[1] + " " + dArr[2];
        }
    }else if(model==="monographunit"){
        return dArr[0] + " " + dArr[1];
    }else if(model==="page"){
        return dArr[0] + " " + dictionary['mods.page.partType.'+dArr[1]];
        
    }else{
        return details;
    }
}


var span = '<span class="ui-icon ui-icon-triangle-1-e folder " >folder</span>';
function getSiblings(obj, pid, model, isLast) {
    var url = "api/item/" + pid + "/siblings";
    $.getJSON(url, function(data) {
        var idx = false;
        $.each(data[0].siblings, function(i, item) {
            if (item.pid != pid) {
                var li2 = addSibling(item.pid, item.model, item.title, true);
                if (idx) {
                    $(obj).parent().append(li2);
                } else {
                    $(obj).before(li2);
                }
            } else {
                idx = true;
            }

        });
        if(isLast){
            setActiveUuids(k4Settings.activePidPath);
        }
        

    });
}
function addSibling(pid, model, title, viewable) {

    var li2 = $('<li style="clear:both;" id="' + model + '_' + pid + '"></li>');
    if (viewable) {
        $(li2).addClass("viewable");
    }
    $(li2).append(span);
    var div = $('<div style="float:left;"><input type="checkbox"  /></div>');
    $(li2).append(div);
    var div2 = $('<div style="float:left;"><a href="#" class="label"><label>' + title + '</label></a></div>');
    $(li2).append(div2);

    return li2;

}

function addNode(node, pid, model_path, model, title, viewable) {

    var nnode = $('<ul></ul>');

    $(node).append(nnode);

    var li = $('<li style="clear:both;" class="' + model + '" id="' + model + '"></li>');
    $(nnode).append(li);

    $(li).append(span);
    var a = $('<a href="#" class="model">' + dictionary['fedora.model.' + model] + '</a>');
    $(li).append(a);
    var ul = $('<ul></ul>');
    $(li).append(ul);
    var li2 = $('<li style="clear:both;" id="' + model_path.replaceAll("/", "-") + '_' + pid + '"></li>');
    if (viewable) {
        $(li2).addClass("viewable");
    }
    $(ul).append(li2);
    $(li2).append(span);
    var div = $('<div style="float:left;"><input type="checkbox"  /></div>');
    $(li2).append(div);
    var div2 = $('<div style="float:left;"><a href="#" class="label"><label>' + title + '</label></a></div>');
    $(li2).append(div2);

    return li2;

}

function nodeClick(obj) {
    if ($(obj).hasClass('viewable')) {
        var id = $(obj).parent().parent().attr('id');
        selectNodeView(id);
        nodeOpen(id);
        if (window.location.hash != id) {
            window.location.hash = id;
        }
        $(".viewer").trigger('viewChanged', [id]);
    } else {
        nodeToggle($(obj).parent().parent());
    }
}

function nodeToggle(obj) {
    $(obj).children("ul").toggle();
    $(obj).children(".folder").toggleClass("ui-icon-triangle-1-s");
}



function highLigthNode(id) {
    $(jq(id) + ">div>a").addClass('sel');
    $(jq(id)).addClass('sel');
    $(jq(id) + ">div>a").addClass('ui-state-active');
    if ($(jq(id)).parent().parent().is('li')) {
        highLigthNode($($(jq(id)).parent().parent()).attr('id'));
    }
}

function showNode(id) {
    $(jq(id) + ">ul").show();
    $(jq(id) + ">span.folder").addClass('ui-icon-triangle-1-s');
    $(jq(id) + ">div>a").addClass('sel');
    $(jq(id) + ">div>a").addClass('ui-state-active');
    $(jq(id)).addClass('sel');
    if ($(jq(id)).parent().parent().is('li')) {
        showNode($($(jq(id)).parent().parent()).attr('id'));
    }
}

function selectBranch(id) {
    var node = $(jq(id));
    if (node.hasClass('viewable')) {
        selectNodeView(id);
        nodeOpen(id);
        if ($(jq(id) + ">ul").html().trim().length > 0) {
            $(jq(id) + ">ul").show();
            $(jq(id) + ">span.folder").toggleClass('ui-icon-triangle-1-s');
        }


        $(".viewer").trigger('viewChanged', [id]);
    } else {
        nodeOpen(id);
        if ($(jq(id) + ">ul").html().trim().length > 0) {
            $(jq(id) + ">ul").show();
            $(jq(id) + ">span.folder").toggleClass('ui-icon-triangle-1-s');
            var id1 = $(node).find('>ul>li>ul>li:first');
            if (id1.length > 0) {
                selectBranch(id1.attr("id"));
            }
        }

    }
}

function selectNodeView(id) {
    var currentLevel = k4Settings.activePidPath.split("_")[0].split("-").length;
    var newLevel = id.split("_")[0].split("-").length;
    var changeLevel = currentLevel != newLevel;
    var fire = false;
    if (!$(jq(id)).parent().parent().hasClass('sel') ||
            ($(jq(id)).hasClass('viewable') && changeLevel)) {
        fire = true;
    }
    $("#item_tree li>div>a").removeClass('sel');
    $("#item_tree li").removeClass('sel');
    $("#item_tree li>div>a").removeClass('ui-state-active');
    $("#item_tree li").removeClass('ui-state-active');
    highLigthNode(id);
    if (fire) {
        setActiveUuids(id);
    }
    setSelectedPath(id);

}

function checkHashChanged(e) {
    var id = k4Settings.activePidPath;
    var newid = window.location.hash.toString().substring(1);
    //alert(newid + " \n" + id);
    if (id != newid) {
        if (newid.length == 0) {
            loadInitNodes();
        }
        //nodeClick(newid);
        nodeClick($(jq(newid).children("div>a")));
    }
}

function nodeOpen(id) {
    if ($(jq(id) + ">ul").length > 0) {
        if ($(jq(id) + ">ul").html().trim().length > 0) {
            $(jq(id) + ">ul").toggle();
        }

    } else {
        loadTreeNode(id);
    }
    $(jq(id) + ">span.folder").toggleClass('ui-icon-triangle-1-s');
}

function loadTreeNode(id) {
    var pid = id.split('_')[1];

    var path = id.split('_')[0];
    var url = 'details/treeNode.vm?pid=' + pid + '&model_path=' + path;
    $.get(url, function(data) {
        var d = trim10(data);
        if (d.length > 0) {
            $(jq(id)).append(d);
            if ($(jq(id) + ">ul").html() == null || $(jq(id) + ">ul").html().trim().length == 0) {
                $(jq(id) + ">ul").hide();
            }
        } else {
            $(jq(id) + ">span.folder").removeClass();
        }
    });
}

function setActiveUuids(id) {
    k4Settings.activePidPath = id;
    k4Settings.activeUuids = [];

    var i = 0;
    $(jq(id)).parent().children('li').each(function() {
        k4Settings.activeUuids[i] = $(this).attr('id');
        i++;
    });

    $(".viewer").trigger('activeUuidsChanged', [id]);
}

function getPidPath(id) {
    var curid = id;
    var selectedPathTemp = "";
    while ($(jq(curid)).parent().parent().is('li')) {
        if (!$(jq(curid)).hasClass('model')) {
            if (selectedPathTemp != "")
                selectedPathTemp = "/" + selectedPathTemp;
            selectedPathTemp = curid.split('_')[1] + selectedPathTemp;
        }
        curid = $($(jq(curid)).parent().parent()).attr('id');
    }
    return selectedPathTemp;
}

function setSelectedPath(id) {
    var curid = id;
    var selectedPathTemp = [];
    var i = 0;
    while ($(jq(curid)).parent().parent().is('li')) {
        if (!$(jq(curid)).hasClass('model')) {
            selectedPathTemp.push(curid);
        }
        curid = $($(jq(curid)).parent().parent()).attr('id');
        i++;
    }
    selectedPathTemp.reverse();
    var level = selectedPathTemp.length - 1;
    for (var j = 0; j < selectedPathTemp.length; j++) {
        if (k4Settings.selectedPath[j] != selectedPathTemp[j]) {
            level = j;
            break;
        }
    }
    k4Settings.selectedPath = [];
    k4Settings.selectedPathTexts = [];
    for (var j = 0; j < selectedPathTemp.length; j++) {
        k4Settings.selectedPath[j] = selectedPathTemp[j];
        var html = $(jq(selectedPathTemp[j]) + ">div>a>label").html();
        if (html == null)
            html = '';
        k4Settings.selectedPathTexts[j] = html;
    }
    $(".viewer").trigger('selectedPathChanged', [level]);
}

function toggleRightMenu(speed) {
    if (speed) {
        $('#rightMenuBox').toggle(2000);
    } else {
        $('#rightMenuBox').toggle();
    }
    $('#showHideRightMenu>a>span').toggleClass('ui-icon-circle-triangle-w');
}

function showContextMenu() {
    $('#item_tree input:checked').each(function() {
        var id = $(this).parent().parent().attr("id");
        $('#context_items').append('<div>' + id + '</div>');
    });

    $('#contextMenu').show();
    $('#item_tree').hide();
    $('#searchInside').hide();
}

function showStructure() {
    $('#searchInside').hide();
    $('#contextMenu').hide();
    $('#item_tree').show();
}

function closeSearchInside() {
    $('#searchInside').hide();
    $('#contextMenu').hide();
    $('#item_tree').show();
}

function searchInside(start) {
    var offset = start ? start : 0;

    //$('#contextMenu').hide();
    //$('#item_tree').hide();
    //$('#searchInside').show();
    $('#searchInsideResults').html('<img alt="loading" src="img/loading.gif" />');
    var q = $('#insideQuery').val();
    var fq = "";
    if ($('#item_tree input:checked').length > 0) {
        $('#item_tree input:checked').each(function() {
            var id = $(this).parent().parent().attr("id");//.split('_')[1];
            if (fq != "") {
                fq += " OR ";
            }
            fq += "pid_path:" + getPidPath(id).replace(":", "\\:") + "*";
        });
        fq = "&fq=" + fq;
    } else {
        var fqval = $('#item_tree>li>ul>li:first').attr("id").split('_')[1];
        fq = "&fq=pid_path:" + fqval.replace(":", "\\:") + "*";
    }

    var url = "inc/details/searchInside.vm?q=" + q + "&offset=" + offset + "&xsl=insearch.xsl&collapsed=false&facet=false" + fq;
    $.get(url, function(data) {
        $('#searchInsideResults').html(data);
    });
}

var inputInitialized = false;
function checkInsideInput() {
    var iniVal = '$text.administrator.menu.searchinside';
    var q = $('#insideQuery').val();
    if (!inputInitialized && iniVal == q) {
        inputInitialized = true;
        $('#insideQuery').val('');
    }
}

function checkEnter(evn) {
    if (window.event && window.event.keyCode == 13) {
        searchInside(0);
    } else if (evn && evn.keyCode == 13) {
        searchInside(0);
    }
}

function getTreeSelection() {
    var uuids = [];
    $('#item_tree input:checked').each(function() {
        var id = $(this).parent().parent().attr("id");
        uuids.push(id);
    });
    return uuids;
}

function checkDonator(id) {
    $.get('inc/details/donator.vm?uuid=' + k4Settings.selectedPath[0].split("_")[1], function(data) {
        $('#donator').html(data);
    });
}


function getSuggested(viewerOptions) {
    $.get('inc/details/suggest.vm?pid=' + viewerOptions.pid +
            "&pid_path=" + getPidPath(k4Settings.activePidPath), function(data) {
        $('#suggest>div.content').html(data);
    });
}

