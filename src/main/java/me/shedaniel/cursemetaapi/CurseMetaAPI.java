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
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author shedaniel
 */
public class CurseMetaAPI {
    
    public static final String API = "https://addons-ecs.forgesvc.net";
    public static final Gson GSON = new GsonBuilder().create();
    
    /**
     * Returns the addon by the addon id.
     *
     * @param id the addon id
     * @return the addon, returns null if error
     */
    public static Addon getAddon(int id) {
        try {
            URL url = new URL(API + "/api/v2/addon/" + id);
            Addon object = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(url)), Addon.class);
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * @param ids the addon id
     * @return the list of addons, return empty list if error
     * @throws NullPointerException if > 50 addons
     */
    public static List<Addon> getAddons(int... ids) {
        try {
            List<Addon> addons = new ArrayList<>();
            for(int id : ids) {
                Addon addon = getAddon(id);
                if (addon != null)
                    addons.add(addon);
            }
            return addons;
        } catch (Exception e) {
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
            return GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v2/addon/" + addon + "/file/" + fileId))), AddonFile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the addon file changelog.
     *
     * @param addon  the addon id
     * @param fileId the file id
     * @return the changelog in html, returns null if error
     */
    public static String getAddonFileChangelog(int addon, int fileId) {
        try {
            return InternetUtils.getSite(new URL(API + "/api/v2/addon/" + addon + "/file/" + fileId + "/changelog"));
        } catch (IOException e) {
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
            List<AddonFile> files = new ArrayList<>();
            for(int i = 0; i < addons.length; i++) {
                int addon = addons[i];
                int fileId = fileIds[i];
                AddonFile file = getAddonFile(addon, fileId);
                if (file != null)
                    files.add(file);
            }
            return files;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    public static List<AddonFile> getAddonFiles(int addonId) {
        try {
            JsonArray array = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v2/addon/" + addonId + "/files"))), JsonArray.class);
            List<AddonFile> files = new ArrayList<>();
            array.forEach(jsonElement -> {
                if (jsonElement.isJsonObject())
                    try {
                        files.add(GSON.fromJson(jsonElement, AddonFile.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    
    static List<Addon> search(MetaSearch metaSearch) {
        try {
            String args = "";
            args += "&index=" + metaSearch.getPageIndex();
            args += "&pageSize=" + metaSearch.getPageSize();
            args += "&sort=" + URLEncoder.encode(metaSearch.getSort().getName(), "UTF-8");
            args += "&isSortDescending=" + metaSearch.getSortDescending();
            if (metaSearch.getSectionId() != null)
                args += "&sectionId=" + metaSearch.getSectionId();
            if (metaSearch.getCategoryId() != null)
                args += "&categoryId=" + metaSearch.getCategoryId();
            if (metaSearch.getGameVersion() != null)
                args += "&gameVersion=" + URLEncoder.encode(metaSearch.getGameVersion(), "UTF-8");
            if (metaSearch.getSearchFilter() != null)
                args += "&searchFilter=" + URLEncoder.encode(metaSearch.getSearchFilter(), "UTF-8");
            JsonArray array = GSON.fromJson(new InputStreamReader(InternetUtils.getSiteStream(new URL(API + "/api/v2/addon/search?gameId=" + metaSearch.getGameId() + args))), JsonArray.class);
            List<Addon> addons = new ArrayList<>();
            array.forEach(jsonElement -> {
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
     * @param id the project id
     * @return the html in a form of a String, returns null if error
     */
    public static String getAddonDescription(int id) {
        try {
            return InternetUtils.getSite(new URL(API + "/api/v2/addon/" + id + "/description"));
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
        
        public static void downloadToFile(URL url, File file) throws IOException {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
        
    }
    
    public static class Addon {
        private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        public int id;
        public String name;
        public List<AddonAuthor> authors;
        public List<AddonAttachment> attachments;
        public String websiteUrl;
        public int gameId;
        public String summary;
        @SerializedName("defaultFileId") public int latestReleaseFileId;
        public double downloadCount;
        public List<AddonLatestFiles> latestFiles;
        public List<AddonCategory> categories;
        public int status;
        public int primaryCategoryId;
        public AddonCategorySection categorySection;
        public String slug;
        public List<AddonGameVersionFiles> gameVersionLatestFiles;
        public boolean isFeatured;
        public float popularityScore;
        public int gamePopularityRank;
        public String primaryLanguage;
        public String gameSlug;
        public String gameName;
        public String portalName;
        public String dateModified;
        public String dateCreated;
        public String dateReleased;
        public boolean isAvailable;
        @SerializedName("isExperiemental") public boolean isExperimental;
        
        public Date getDateCreated() throws ParseException {
            return format.parse(dateCreated + "Z");
        }
        
        public Date getDateModified() throws ParseException {
            return format.parse(dateModified + "Z");
        }
        
        public Date getDateReleased() throws ParseException {
            return format.parse(dateReleased + "Z");
        }
        
        public static class AddonAttachment {
            public int id;
            public int projectId;
            public String description;
            public boolean isDefault;
            public String thumbnailUrl;
            @SerializedName("title") public String attachmentName;
            public String url;
            public int status;
        }
        
        public static class AddonAuthor {
            public String name;
            public String url;
            public int projectId;
            public int id;
            public int userId;
            public int twitchId;
        }
        
        public static class AddonCategory {
            public int categoryId;
            public String name;
            public String url;
            public String avatarUrl;
            public int parentId;
            public int rootId;
            public int projectId;
            public int avatarId;
            public int gameId;
        }
        
        public static class AddonCategorySection {
            public int id;
            public int gameId;
            public String name;
            public int packageType;
            public String path;
            public String initialInclusionPattern;
            public String extraIncludePattern;
            public int gameCategoryId;
        }
        
        public static class AddonGameVersionFiles {
            public String gameVersion;
            public int projectFileId;
            public String projectFileName;
            public int fileType;
        }
        
        public static class AddonLatestFiles {
            public int id;
            public String displayName;
            public String fileName;
            public String fileDate;
            public long fileLength;
            public int releaseType;
            public int fileStatus;
            public String downloadUrl;
            public boolean isAlternate;
            public int alternateFileId;
            public List<FileDependency> dependencies;
            public boolean isAvailable;
            public List<FileModules> modules;
            public long packageFingerprint;
            public List<String> gameVersion;
            public List<SortableGameVersion> sortableGameVersion;
            public JsonElement installMetadata;
            public JsonElement changelog;
            public boolean hasInstallScript;
            public boolean isCompatibleWithClient;
            public int categorySectionPackageType;
            public int restrictProjectFileAccess;
            public int projectStatus;
            public int renderCacheId;
            public JsonElement fileLegacyMappingId;
            public int projectId;
            public JsonElement parentProjectFileId;
            public JsonElement parentFileLegacyMappingId;
            public JsonElement fileTypeId;
            public JsonElement exposeAsAlternative;
            public long packageFingerprintId;
            public String gameVersionDateReleased;
            public int gameVersionMappingId;
            public int gameVersionId;
            public int gameId;
            public boolean isServerPack;
            public JsonElement serverPackFileId;
            
            public static class FileDependency {
                public int id;
                public int addonId;
                public int type;
                public int fileId;
            }
            
            public static class FileModules {
                @SerializedName("foldername") public String moduleName;
                public long fingerprint;
                public int type;
            }
            
            public static class SortableGameVersion {
                public String gameVersionPadded;
                public String gameVersion;
                public String gameVersionReleaseDate;
                public String gameVersionName;
            }
        }
    }
    
    public static class AddonFile {
        @SerializedName("id") public int fileId;
        public String displayName;
        public String fileName;
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
        public JsonElement installMetadata;
        public JsonElement serverPackFileId;
        public boolean hasInstallScript;
        
        public static class FileDependency {
            public int addonId;
            public int type;
        }
        
        public static class FileModule {
            public String folderName;
            public long fingerprint;
        }
    }
    
}
