/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.example.samplestickerapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

@SuppressWarnings("FieldCanBeLocal")
public class WhitelistCheck {
    private static final String AUTHORITY_QUERY_PARAM = "authority";
    private static final String IDENTIFIER_QUERY_PARAM = "identifier";
    private static String STICKER_APP_AUTHORITY = BuildConfig.CONTENT_PROVIDER_AUTHORITY;
    public static String CONSUMER_WHATSAPP_PACKAGE_NAME = "com.whatsapp";
    public static String SMB_WHATSAPP_PACKAGE_NAME = "com.whatsapp.w4b";
    private static String CONTENT_PROVIDER = ".provider.sticker_whitelist_check";
    private static String QUERY_PATH = "is_whitelisted";
    private static String QUERY_RESULT_COLUMN_NAME = "result";

    static boolean isWhitelisted(@NonNull Context context, @NonNull String identifier) {
        try {
            if (!isWhatsAppConsumerAppInstalled(context.getPackageManager()) && !isWhatsAppSmbAppInstalled(context.getPackageManager())) {
                return false;
            }
            boolean consumerResult = isStickerPackWhitelistedInWhatsAppConsumer(context, identifier);
            boolean smbResult = isStickerPackWhitelistedInWhatsAppSmb(context, identifier);
            return consumerResult && smbResult;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isWhitelistedFromProvider(@NonNull Context context, @NonNull String identifier, String whatsappPackageName) {
        final PackageManager packageManager = context.getPackageManager();
        if (isPackageInstalled(whatsappPackageName, packageManager)) {
            final String whatsappProviderAuthority = whatsappPackageName + CONTENT_PROVIDER;
            final ProviderInfo providerInfo = packageManager.resolveContentProvider(whatsappProviderAuthority, PackageManager.GET_META_DATA);
            // provider is not there. The WhatsApp app may be an old version.
            if (providerInfo == null) {
                return false;
            }
            final Uri queryUri = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(whatsappProviderAuthority).appendPath(QUERY_PATH).appendQueryParameter(AUTHORITY_QUERY_PARAM, STICKER_APP_AUTHORITY).appendQueryParameter(IDENTIFIER_QUERY_PARAM, identifier).build();
            try (final Cursor cursor = context.getContentResolver().query(queryUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    final int whiteListResult = cursor.getInt(cursor.getColumnIndexOrThrow(QUERY_RESULT_COLUMN_NAME));
                    return whiteListResult == 1;
                }
            }
        } else {
            //if app is not installed, then don't need to take into its whitelist info into account.
            return true;
        }
        return false;
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            //noinspection SimplifiableIfStatement
            if (applicationInfo != null) {
                return applicationInfo.enabled;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isWhatsAppConsumerAppInstalled(PackageManager packageManager) {
        return WhitelistCheck.isPackageInstalled(CONSUMER_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    public static boolean isWhatsAppSmbAppInstalled(PackageManager packageManager) {
        return WhitelistCheck.isPackageInstalled(SMB_WHATSAPP_PACKAGE_NAME, packageManager);
    }

    public static boolean isStickerPackWhitelistedInWhatsAppConsumer(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, CONSUMER_WHATSAPP_PACKAGE_NAME);
    }

    public static boolean isStickerPackWhitelistedInWhatsAppSmb(@NonNull Context context, @NonNull String identifier) {
        return isWhitelistedFromProvider(context, identifier, SMB_WHATSAPP_PACKAGE_NAME);
    }
}
