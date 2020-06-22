package moe.sqwatermark;

/**
 * 定义了一个天体
 */
public class Body {

    // 质量 太阳质量
    public double mass;

    // 坐标 天文单位
    public double x;
    public double y;
    public double z;

    // 速度 天文单位/年
    public double vx;
    public double vy;
    public double vz;

    public Body(double mass, double x, double y, double z, double vx, double vy, double vz) {
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }

    /**
     * 移动
     * @param dt 时间步长
     */
    public void move(double dt) {
        this.x = this.x + this.vx * dt;
        this.y = this.y + this.vy * dt;
        this.z = this.z + this.vz * dt;
    }

    /**
     * 加速
     * @param ax x方向加速度
     * @param ay y方向加速度
     * @param az z方向加速度
     * @param dt 时间步长
     */
    private void accelerate(double ax, double ay, double az, double dt) {
        this.vx = this.vx + ax * dt;
        this.vy = this.vy + ay * dt;
        this.vz = this.vz + az * dt;
    }

    public void attractBy(Body body, double distance, double dt) {
        double a = World.K * body.mass / Math.pow(distance, 2);
        double ax = a * (body.x-this.x) / distance;
        double ay = a * (body.y-this.y) / distance;
        double az = a * (body.z-this.z) / distance;
        this.accelerate(ax, ay, az, dt);
    }

    public double getDistance(double x, double y, double z) {
        return Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2) + Math.pow(this.z - z, 2));
    }

    public double getSquaredVelocity() {
        return Math.pow(vx, 2) + Math.pow(vy, 2) + Math.pow(vz, 2);
    }

    public double getKineticEnergy() {
        return 0.5 * mass * this.getSquaredVelocity();
    }

}
