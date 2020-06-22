package moe.sqwatermark;

public class World {

    //引力常数
    public static final double G = 6.67259e-11;
    //太阳质量
    public static final double M_SUN = 1.9891e30;
    //天文单位
    public static final double AU = 1.4959787e11;
    //一年的秒数
    public static final int SECS = 31536000;
    //以天文单位为长度单位，太阳质量为质量单位，一年为时间单位得出的引力常数
    public static final double K = G * M_SUN / (Math.pow(AU, 3) / Math.pow(SECS, 2));

    //时间步长
    public static final double dt = 0.001;

    //当前时刻
    public double time = 0;

    /*
    日地月三体，时间步长为0.01的情况下跑了4937年，月球逃逸
    时间步长为0.005的情况下跑了一千万年以上
    public Body body1 = new Body(1, 0, 0, 0, 0, 0, 0);
    public Body body2 = new Body(3e-6, 1, 0, 0, 0, 6.29666, 0);
    public Body body3 = new Body(3.7e-8, 1.00257, 0, 0, 0, 6.51210, 0);
     */

    public Body body1;
    public Body body2;
    public Body body3;

    public World(double[] args) {
        //初始参数以body1为参照系，三体质量均为太阳质量，运动固定在xoy平面，则需要确定body2和body3的速度和位置，共8个值
        body1 = new Body(1, 0, 0, 0, 0, 0, 0);
        body2 = new Body(1, args[0], args[1], 0, args[2], args[3], 0);
        body3 = new Body(1, args[4], args[5], 0, args[6], args[7], 0);
        //求质心位置
        final double centerX = (body1.mass * body1.x + body2.mass * body2.x + body3.mass * body3.x) / (body1.mass + body2.mass + body3.mass);
        final double centerY = (body1.mass * body1.y + body2.mass * body2.y + body3.mass * body3.y) / (body1.mass + body2.mass + body3.mass);
        final double centerZ = (body1.mass * body1.z + body2.mass * body2.z + body3.mass * body3.z) / (body1.mass + body2.mass + body3.mass);
        //以质心为参照系，求相对质心的位置并赋值给天体的位置
        body1.x = body1.x - centerX;
        body1.y = body1.y - centerY;
        body1.z = body1.z - centerZ;
        body2.x = body2.x - centerX;
        body2.y = body2.y - centerY;
        body2.z = body2.z - centerZ;
        body3.x = body3.x - centerX;
        body3.y = body3.y - centerY;
        body3.z = body3.z - centerZ;
        //求质心速度
        final double vCenterX = (body1.mass * body1.vx + body2.mass * body2.vx + body3.mass * body3.vx) / (body1.mass + body2.mass + body3.mass);
        final double vCenterY = (body1.mass * body1.vy + body2.mass * body2.vy + body3.mass * body3.vy) / (body1.mass + body2.mass + body3.mass);
        final double vCenterZ = (body1.mass * body1.vz + body2.mass * body2.vz + body3.mass * body3.vz) / (body1.mass + body2.mass + body3.mass);
        //以质心为参照系，求相对质心的速度并赋值给天体的速度
        body1.vx = body1.vx - vCenterX;
        body1.vy = body1.vy - vCenterY;
        body1.vz = body1.vz - vCenterZ;
        body2.vx = body2.vx - vCenterX;
        body2.vy = body2.vy - vCenterY;
        body2.vz = body2.vz - vCenterZ;
        body3.vx = body3.vx - vCenterX;
        body3.vy = body3.vy - vCenterY;
        body3.vz = body3.vz - vCenterZ;
    }

    public double getLifetime() {
        double distance12;
        double distance23;
        double distance31;
        distance12 = body1.getDistance(body2.x, body2.y, body2.z);
        distance23 = body2.getDistance(body3.x, body3.y, body3.z);
        distance31 = body3.getDistance(body1.x, body1.y, body1.z);
        //计算势能的常数部分，不需要重复计算
        final double C1 = K * body1.mass * (body2.mass + body3.mass) / Math.pow(((body1.mass + body2.mass + body3.mass) / (body2.mass + body3.mass)), 2);
        final double C2 = K * body2.mass * (body1.mass + body3.mass) / Math.pow(((body1.mass + body2.mass + body3.mass) / (body1.mass + body3.mass)), 2);
        final double C3 = K * body3.mass * (body1.mass + body2.mass) / Math.pow(((body1.mass + body2.mass + body3.mass) / (body1.mass + body2.mass)), 2);
        while (true) {
            body1.attractBy(body2, distance12, dt);
            body1.attractBy(body3, distance31, dt);
            body2.attractBy(body1, distance12, dt);
            body2.attractBy(body3, distance23, dt);
            body3.attractBy(body1, distance31, dt);
            body3.attractBy(body2, distance23, dt);
            body1.move(dt);
            body2.move(dt);
            body3.move(dt);
            time = time + dt;
            distance12 = body1.getDistance(body2.x, body2.y, body2.z);
            distance23 = body2.getDistance(body3.x, body3.y, body3.z);
            distance31 = body3.getDistance(body1.x, body1.y, body1.z);
            if (body1.getKineticEnergy() > C1 / body1.getDistance(0, 0, 0)) {
                //Problem.logger.info("body1逃逸");
                return time;
            }
            if (body2.getKineticEnergy() > C2 / body2.getDistance(0, 0, 0)) {
                //Problem.logger.info("body2逃逸");
                return time;
            }
            if (body3.getKineticEnergy() > C3 / body3.getDistance(0, 0, 0)) {
                //Problem.logger.info("body3逃逸");
                return time;
            }
//            if (time > 1e2 && time < 1e2 + 0.002) System.out.println("已达到100年");
//            if (time > 1e3 && time < 1e3 + 0.002) System.out.println("已达到1000年");
//            if (time > 1e4 && time < 1e4 + 0.002) System.out.println("已达到1万年");
//            if (time > 1e5 && time < 1e5 + 0.002) System.out.println("已达到10万年");
//            if (time > 1e6 && time < 1e6 + 0.002) System.out.println("已达到100万年");
//            if (time > 1e7 && time < 1e7 + 0.002) System.out.println("已达到1000万年");
        }
    }
}
