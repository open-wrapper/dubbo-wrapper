package org.apache.dubbo.common.extension.checker;

import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.common.extension.SPIMetaInfo;

import java.util.List;

@SPI("noopSPIChecker")
public interface SPIMetaChecker {
    void check(List<SPIMetaInfo> metaInfos);
}
