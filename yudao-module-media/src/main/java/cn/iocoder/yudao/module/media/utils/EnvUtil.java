package cn.iocoder.yudao.module.media.utils;

public class EnvUtil {

    public static boolean isDockerEnv() {
        return "docker".equals(System.getenv("RUN_ENV"));
    }
}
