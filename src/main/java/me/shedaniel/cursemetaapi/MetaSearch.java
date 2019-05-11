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

import java.util.List;

public class MetaSearch {
    private int gameId;
    private Integer sectionId;
    private Integer categoryId;
    private SortMethod sort;
    private boolean isSortDescending;
    private String gameVersion;
    private int index;
    private int pageSize;
    private String searchFilter;
    
    private MetaSearch(int gameId) {
        this.gameId = gameId;
        this.sort = SortMethod.FEATURED;
        this.pageSize = 50;
        this.index = 0;
        this.isSortDescending = false;
    }
    
    public static MetaSearch create(int gameId) {
        return new MetaSearch(gameId);
    }
    
    public MetaSearch setSortingMethod(SortMethod sort) {
        this.sort = sort;
        return this;
    }
    
    /**
     * @return the list of addons found, returns empty list if error
     */
    public List<CurseMetaAPI.Addon> search() {
        return CurseMetaAPI.search(this);
    }
    
    public int getGameId() {
        return gameId;
    }
    
    public Integer getSectionId() {
        return sectionId;
    }
    
    public MetaSearch setSectionId(int sectionId) {
        this.sectionId = sectionId;
        return this;
    }
    
    public Integer getCategoryId() {
        return categoryId;
    }
    
    public MetaSearch setCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    
    public SortMethod getSort() {
        return sort;
    }
    
    public Boolean getSortDescending() {
        return isSortDescending;
    }
    
    public MetaSearch setSortDescending(boolean sortDescending) {
        isSortDescending = sortDescending;
        return this;
    }
    
    public String getGameVersion() {
        return gameVersion;
    }
    
    public MetaSearch setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
        return this;
    }
    
    public Integer getPageIndex() {
        return index;
    }
    
    public MetaSearch setPageIndex(int index) {
        this.index = index;
        return this;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public MetaSearch setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    
    public String getSearchFilter() {
        return searchFilter;
    }
    
    public MetaSearch setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
        return this;
    }
    
    public static enum SortMethod {
        FEATURED("Featured"),
        POPULARITY("Popularity"),
        LAST_UPDATED("LastUpdated"),
        NAME("Name"),
        AUTHOR("Author"),
        TOTAL_DOWNLOADS("TotalDownloads"),
        CATEGORY("Category"),
        GAME_VERSION("GameVersion");
        private String n;
        
        SortMethod(String n) {
            this.n = n;
        }
        
        String getName() {
            return n;
        }
    }
}
