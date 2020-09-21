/**
 * Created by Administrator on 2014-10-22.
 */

WFEdit.prototype.loadFromWftemp = function (wftempdata) {
    this.removeall();
    for (var i = 0; i < wftempdata.shwwftempprocs.length; i++) {
        var nodedata = wftempdata.shwwftempprocs[i];
        //nodedata.stated = nodedata.stat;
        nodedata.title = nodedata.proctempname;
        nodedata.users = [];
        for (var j = 0; j < nodedata.shwwftempprocusers.length; j++) {
            nodedata.users.push({username: nodedata.shwwftempprocusers[j].displayname});
        }
        this.addNode(nodedata);
    }
    for (var i = 0; i < wftempdata.shwwftemplinklines.length; i++) {
        var linedata = wftempdata.shwwftemplinklines[i];
        if (!linedata.idx) linedata.idx = 0;
        if (linedata.lltitle && (linedata.lltitle.length > 0))
            linedata.title = linedata.lltitle + "(" + linedata.idx + ")";
        else
            linedata.title = linedata.lltitle;
        var fNode = this.findNodeByWftemID(linedata.fromproctempid);
        var tNode = this.findNodeByWftemID(linedata.toproctempid);
        this.addLine(linedata, fNode, tNode);
    }
    this.reSetScroll();
};

WFEdit.prototype.findNodeByWftemID = function (wftid) {
    var nodes = this.nodes();
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        //alert(node.getData().proctempid + " " + wftid);
        if (node.getData().proctempid == wftid)
            return node;
    }
    return null;
};

WFEdit.prototype.loadFromWf = function (wfdata) {
    this.removeall();
    for (var i = 0; i < wfdata.shwwfprocs.length; i++) {
        var nodedata = wfdata.shwwfprocs[i];
        nodedata.title = nodedata.procname;
        nodedata.stated = nodedata.stat;
        nodedata.users = [];
        for (var j = 0; j < nodedata.shwwfprocusers.length; j++) {
            var us = nodedata.shwwfprocusers[j];
            var dt = parseInt(us.stat);
            if (dt == 3) {
                nodedata.users.push({username: us.displayname + "(驳回)"});
            } else if (dt == 2) {
                nodedata.users.push({username: us.displayname + "(完成)"});
            } else
                nodedata.users.push({username: us.displayname});
        }
        this.addNode(nodedata);
    }
    for (var i = 0; i < wfdata.shwwflinklines.length; i++) {
        var linedata = wfdata.shwwflinklines[i];
        linedata.title = linedata.lltitle;
        var fNode = this.findNodeByWfID(linedata.fromprocid);
        var tNode = this.findNodeByWfID(linedata.toprocid);
        this.addLine(linedata, fNode, tNode);
    }
    this.reSetScroll();
};

WFEdit.prototype.findNodeByWfID = function (procid) {
    var nodes = this.nodes();
    for (var i = 0; i < nodes.length; i++) {
        var node = nodes[i];
        //alert(node.getData().proctempid + " " + wftid);
        if (node.getData().procid == procid)
            return node;
    }
    return null;
};