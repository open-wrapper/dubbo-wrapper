package org.apache.dubbo.common.extension.checker.impl;

import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.SPIMetaInfo;
import org.apache.dubbo.common.extension.checker.SPIMetaChecker;

import java.util.List;

@Activate
public class NoopSPIMetaChecker implements SPIMetaChecker {
    @Override
    public void check(List<SPIMetaInfo> metaInfos) {

    }
}
