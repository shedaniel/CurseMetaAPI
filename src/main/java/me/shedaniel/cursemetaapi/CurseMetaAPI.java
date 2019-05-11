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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author shedaniel
 */
public class CurseMetaAPI {
    
    public static final String API = "https://staging_cursemeta.dries007.net/";
    public static final Gson GSON = new GsonBuilder().create();
    
    /**
     * Returns the addon by the addon id.
     *
     * @param id the addon id
     * @return the addon, returns null if error
     */
    public static Addon getAddon(int id) {
        try {
            return getAddons(id).get(0);
        } catch (Exception e) {
        }
        return null;
    }
    
    /**
     * @param ids the addon id
     * @return the list of addons, return empty list if error
     * @throws NullPointerException if > 50 addons
     */
    public static List<Addon> getAddons(int... ids) {
        if (ids.length > 50)
            throw new NullPointerException("Too many addons! Please split them!");
        try {
            String args = ids.length > 0 ? "?" : "";
            for(int i = 0; i < ids.length; i++) {
                if (args.charAt(args.length() - 1) != '?')
                    args += '&';
                args += "id=" + ids[i];
            }
            URL url = new URL(API + "/api/v3/direct/addon" + args);
            JsonArray object = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(url)), JsonArray.class);
            List<Addon> addons = new ArrayList<>();
            object.forEach(jsonElement -> {
                try {
                    addons.add(GSON.fromJson(jsonElement, Addon.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return addons;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    /**
     * @param addon  the addon id
     * @param fileId the file id
     * @return the file, returns null if error
     */
    public static AddonFile getAddonFile(int addon, int fileId) {
        try {
            return GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v3/direct/addon/" + addon + "/file/" + fileId))), AddonFile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param addons  the addons id
     * @param fileIds the files id
     * @return the list of files, return empty list if error
     * @throws NullPointerException if addons' and fileIds' size is the not same
     */
    public static List<AddonFile> getAddonFiles(int[] addons, int fileIds[]) {
        try {
            if (addons.length != fileIds.length)
                throw new NullPointerException();
            String args = addons.length > 0 ? "?" : "";
            for(int i = 0; i < addons.length; i++) {
                if (args.charAt(args.length() - 1) != '?')
                    args += '&';
                args += "addon=" + addons[i] + "&file=" + fileIds[i];
            }
            List<AddonFile> addonFiles = new ArrayList<>();
            JsonObject object = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v3/direct/addon/files" + args))), JsonObject.class);
            object.entrySet().forEach(entry -> {
                try {
                    addonFiles.add(GSON.fromJson(entry.getValue().getAsJsonArray().get(0), AddonFile.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return addonFiles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    /**
     * @param addons  the addons id
     * @param fileIds the files id
     * @return the map of addon id and its file, return empty map if error
     * @throws NullPointerException if addons' and fileIds' size is the not same
     */
    public static Map<String, AddonFile> getAddonFilesMap(int[] addons, int fileIds[]) {
        try {
            if (addons.length != fileIds.length)
                throw new NullPointerException();
            String args = addons.length > 0 ? "?" : "";
            for(int i = 0; i < addons.length; i++) {
                if (args.charAt(args.length() - 1) != '?')
                    args += '&';
                args += "addon=" + addons[i] + "&file=" + fileIds[i];
            }
            Map<String, AddonFile> addonFiles = new LinkedHashMap<>();
            JsonObject object = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v3/direct/addon/files" + args))), JsonObject.class);
            object.entrySet().forEach(entry -> {
                try {
                    addonFiles.put(entry.getKey(), GSON.fromJson(entry.getValue().getAsJsonArray().get(0), AddonFile.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return addonFiles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
    
    /**
     * @param id the project id
     * @return the html in a form of a String, returns null if error
     */
    public static String getAddonDescription(int id) {
        try {
            return InternetUtils.getSite(new URL(API + "/api/v3/direct/addon/" + id + "/description"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static class InternetUtils {
        
        public static final String USER_AGENT = "Mozilla/5.0";
        
        public static String getSite(URL url) throws IOException {
            return getStringFromStream(getSiteStream(url));
        }
        
        public static InputStream getSiteStream(URL url) throws IOException {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            return con.getInputStream();
        }
        
        public static String getStringFromStream(InputStream stream) throws IOException {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader in = new BufferedReader(inputStreamReader);
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            stream.close();
            return response.toString();
        }
        
    }
    
    public static class Addon {
        public int id;
        public String name;
        public List<AddonAuthor> authors;
        public String websiteUrl;
        public int gameId;
        public String summary;
        @SerializedName("defaultFileId") public int latestReleaseFileId;
        public int commentCount;
        public float downloadCount;
        public int rating;
        public int installCount;
        public List<AddonCategory> categories;
        public String primaryAuthorName;
        public String externalUrl;
        public int status;
        public int stage;
        public String donationUrl;
        public String primaryCategoryName;
        public String primaryCategoryAvatarUrl;
        public int likes;
        public int packageType;
        public String avatarUrl;
        public String slug;
        public String clientUrl;
        public List<AddonGameVersionFiles> gameVersionLatestFiles;
        public int isFeatured;
        public float popularityScore;
        public int gamePopularityRank;
        public String primaryLanguage;
        public String fullDescription;
        public String gameName;
        public String portalName;
        public String sectionName;
        public String dateModified;
        public String dateCreated;
        public String dateReleased;
        public boolean isAvailable;
        public String categoryList;
        
        public static class AddonAuthor {
            public String name;
            public String url;
            public int twitchId;
        }
        
        public static class AddonCategory {
            public int id;
            public String name;
            public String url;
            public String avatarUrl;
            public int parentId;
            public int rootId;
        }
        
        public static class AddonCategorySection {
            @SerializedName("Id") public int id;
            public int gameId;
            public String name;
            public int packageType;
            public String path;
            public String initialInclusionPattern;
            public String extraIncludePattern;
        }
        
        public static class AddonGameVersionFiles {
            public String gameVersion;
            public int projectFileId;
            public String projectFileName;
            public int fileType;
        }
    }
    
    public static class AddonFile {
        @SerializedName("id") public int fileId;
        public String fileName;
        public String fileNameOnDisk;
        public String fileDate;
        public int fileLength;
        public int releaseType;
        public int fileStatus;
        public String downloadUrl;
        public boolean isAlternate;
        public int alternateFileId;
        public List<FileDependency> dependencies;
        public boolean isAvailable;
        public List<FileModule> modules;
        public long packageFingerprint;
        public List<String> gameVersion;
        public Object installMetadata;
        
        public static class FileDependency {
            public int addonId;
            public int type;
        }
        
        public static class FileModule {
            public String folderName;
            @SerializedName("fimgerprint") public long fingerprint;
        }
    }
    
}
