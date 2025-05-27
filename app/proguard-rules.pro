# Security-focused ProGuard rules for air-gapped deployment

# Keep security-related classes
-keep class io.github.utkarshvishnoi.zeroxqr.security.** { *; }

# Keep biometric and keystore related classes
-keep class androidx.biometric.** { *; }
-keep class android.security.keystore.** { *; }

# Keep cryptography classes
-keep class javax.crypto.** { *; }
-keep class java.security.** { *; }

# Remove logging in release builds for security
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Obfuscate everything else for security
-repackageclasses 'obfuscated'

# Keep application class
-keep class io.github.utkarshvishnoi.zeroxqr.ZeroXQRApplication { *; }