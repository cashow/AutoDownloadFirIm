package com.cashow.autodownloadfir.main.model;

import java.util.ArrayList;
import java.util.List;

public class FirInfoList {
    public List<FirInfo> infoList;
    public boolean isAutoDownload;

    public FirInfoList() {
        infoList = new ArrayList<>();
        infoList.add(new FirInfo("", true, ""));
    }
}
