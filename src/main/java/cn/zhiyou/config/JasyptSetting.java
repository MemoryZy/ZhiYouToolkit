package cn.zhiyou.config;

import cn.hutool.core.util.StrUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Memory
 * @since 2024/1/23
 */
// @State(name = "ZhiYouJasypt", storages = {@Storage(value = "ZhiYouJasypt.xml")})
@State(name = "ZhiYouJasypt")
public class JasyptSetting implements PersistentStateComponent<JasyptSetting> {
    /**
     * 对称加密
     */
    public static final String symmetrical = "symmetrical";
    /**
     * 非对称加密
     */
    public static final String asymmetric = "asymmetric";
    public static final String DER = "DER";
    public static final String PEM = "PEM";

    public static JasyptSetting getInstance(Project project) {
        return project.getService(JasyptSetting.class);
    }

    /**
     * 加密方式：对称、非对称
     */
    public String encryption;

    // -------- 对称加密
    public String password;
    public String algorithm;
    public String ivGenerator;

    // -------- 非对称加密
    public String privateKeyFormat;
    public String publicKey;
    public String privateKey;

    @Override
    public @Nullable JasyptSetting getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JasyptSetting state) {
        this.encryption = state.encryption;
        this.password = state.password;
        this.algorithm = state.algorithm;
        this.ivGenerator = state.ivGenerator;
        this.privateKeyFormat = state.privateKeyFormat;
        this.publicKey = state.publicKey;
        this.privateKey = state.privateKey;
    }

    public static boolean validateFullConfig(Project project) {
        JasyptSetting jasyptSetting = JasyptSetting.getInstance(project);
        String encryptionStr = jasyptSetting.encryption;

        if (StrUtil.isBlank(encryptionStr)) {
            return false;
        }

        if (Objects.equals(encryptionStr, symmetrical)) {
            // 默认检查对称
            String passwordStr = jasyptSetting.password;
            String algorithmStr = jasyptSetting.algorithm;
            String ivGeneratorStr = jasyptSetting.ivGenerator;
            return StrUtil.isNotBlank(passwordStr) && StrUtil.isNotBlank(algorithmStr) && StrUtil.isNotBlank(ivGeneratorStr);
        } else {
            String privateKeyFormatStr = jasyptSetting.privateKeyFormat;
            String publicKeyStr = jasyptSetting.publicKey;
            String privateKeyStr = jasyptSetting.privateKey;
            return StrUtil.isNotBlank(publicKeyStr) && StrUtil.isNotBlank(privateKeyStr) && StrUtil.isNotBlank(privateKeyFormatStr);
        }
    }

}
