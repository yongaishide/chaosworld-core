package com.yongaishide.chaosworld.client.mmcr;

/**
 * 单位球网格（半径 1），「人造恒星」的程序化球体几何。
 * <p>
 * 数据布局：每个 quad 4 个顶点，每顶点 5 个 float（x, y, z, u, v）；每个 quad 一个法线（3 个 float）。
 * 顶点按「从球外看逆时针」排列，{@code (phi0,θ0) → (phi0,θ1) → (phi1,θ1) → (phi1,θ0)}，
 * 法线指向球外，所以 <b>dot(法线, 视线) &lt; 0</b> 即背面，可在提交前剔除。
 * </p>
 */
final class StarSphere {

    static final int RINGS = 48;
    static final int SEGMENTS = 96;

    final float[] vertices;
    final float[] normals;
    final int quads;

    private StarSphere(float[] vertices, float[] normals, int quads) {
        this.vertices = vertices;
        this.normals = normals;
        this.quads = quads;
    }

    static StarSphere create(int rings, int segments) {
        int quads = rings * segments;
        float[] vertices = new float[quads * 4 * 5];
        float[] normals = new float[quads * 3];
        int w = 0;
        int n = 0;

        for (int r = 0; r < rings; r++) {
            double phi0 = Math.PI * r / rings;
            double phi1 = Math.PI * (r + 1) / rings;
            double v0 = 1.0D - (double) r / rings;
            double v1 = 1.0D - (double) (r + 1) / rings;

            for (int s = 0; s < segments; s++) {
                double theta0 = 2.0D * Math.PI * s / segments;
                double theta1 = 2.0D * Math.PI * (s + 1) / segments;
                double u0 = (double) s / segments;
                double u1 = (double) (s + 1) / segments;

                w = put(vertices, w, phi0, theta0, u0, v0);
                w = put(vertices, w, phi0, theta1, u1, v0);
                w = put(vertices, w, phi1, theta1, u1, v1);
                w = put(vertices, w, phi1, theta0, u0, v1);

                double phiM = (phi0 + phi1) * 0.5D;
                double thetaM = (theta0 + theta1) * 0.5D;
                float nx = (float) (Math.sin(phiM) * Math.cos(thetaM));
                float ny = (float) Math.cos(phiM);
                float nz = (float) (Math.sin(phiM) * Math.sin(thetaM));
                float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                if (len < 1.0E-6F) {
                    len = 1.0F;
                }
                normals[n++] = nx / len;
                normals[n++] = ny / len;
                normals[n++] = nz / len;
            }
        }

        return new StarSphere(vertices, normals, quads);
    }

    private static int put(float[] out, int index, double phi, double theta, double u, double v) {
        out[index] = (float) (Math.sin(phi) * Math.cos(theta));
        out[index + 1] = (float) Math.cos(phi);
        out[index + 2] = (float) (Math.sin(phi) * Math.sin(theta));
        out[index + 3] = (float) u;
        out[index + 4] = (float) v;
        return index + 5;
    }
}
