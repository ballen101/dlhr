var Point = function (x, y) {
    this.x = x;
    this.y = y;
};
var Polygon = function (centerX, centerY, radius, sides, startAngle, strokeStyle, fillStyle, filled) {
    this.x = centerX;//外接圆心x坐标
    this.y = centerY;
    this.radius = radius;//外接圆半径
    this.sides = sides;//边数
    this.startAngle = startAngle;//开始角度
    this.strokeStyle = strokeStyle;
    this.fillStyle = fillStyle;
    this.filled = filled;
};
Polygon.prototype = {
    getPoints: function () {//获取多边形所有顶点
        var points = [],
            angle = this.startAngle || 0;
        for (var i = 0; i < this.sides; ++i) {
            points.push(new Point(this.x + this.radius * Math.sin(angle),
                this.y - this.radius * Math.cos(angle)));
            angle += 2 * Math.PI / this.sides;
        }
        return points;
    },
    createPath: function (context) {//创建多边形路径
        var points = this.getPoints();
        context.beginPath();
        context.moveTo(points[0].x, points[0].y);
        for (var i = 1; i < this.sides; ++i) {
            context.lineTo(points[i].x, points[i].y);
        }
        context.closePath();
    },
    stroke: function (context) {//对多边形描边
        context.save();
        this.createPath(context);
        context.strokeStyle = this.strokeStyle;
        context.stroke();
        context.restore();
    },
    fill: function (context) {//填充
        context.save();
        this.createPath(context);
        context.fillStyle = this.fillStyle;
        context.fill();
        context.restore();
    },
    move: function (x, y) {
        this.x = x;
        this.y = y;
    },
};