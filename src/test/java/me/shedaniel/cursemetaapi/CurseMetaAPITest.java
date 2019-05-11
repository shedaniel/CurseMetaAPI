/*
 * Copyright (c) 2019 Danielshe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.shedaniel.cursemetaapi;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.util.List;

public class CurseMetaAPITest {
    public static void main(String[] args) {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        List<CurseMetaAPI.Addon> addons = CurseMetaAPI.getAddons(310111, 32274, 311812);
        for(CurseMetaAPI.Addon addon : addons)
            System.out.println(addon.name);
        List<CurseMetaAPI.AddonFile> addonFiles = CurseMetaAPI.getAddonFiles(new int[]{32274}, new int[]{2709306});
        for(CurseMetaAPI.AddonFile addonFile : addonFiles)
            System.out.println(addonFile.fileNameOnDisk);
    }
}