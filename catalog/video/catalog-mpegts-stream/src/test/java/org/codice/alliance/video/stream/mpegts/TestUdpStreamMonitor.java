/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.video.stream.mpegts;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.codice.alliance.libs.klv.KlvHandler;
import org.codice.alliance.libs.klv.KlvHandlerFactory;
import org.codice.alliance.libs.klv.KlvProcessor;
import org.codice.alliance.libs.klv.Stanag4609Processor;
import org.codice.alliance.video.stream.mpegts.filename.FilenameGenerator;
import org.codice.alliance.video.stream.mpegts.netty.UdpStreamProcessor;
import org.codice.alliance.video.stream.mpegts.rollover.RolloverCondition;
import org.junit.Before;
import org.junit.Test;

import ddf.catalog.CatalogFramework;
import ddf.catalog.data.MetacardType;

public class TestUdpStreamMonitor {

    private UdpStreamProcessor udpStreamProcessor;

    private UdpStreamMonitor udpStreamMonitor;

    @Before
    public void setup() {
        udpStreamProcessor = mock(UdpStreamProcessor.class);
        udpStreamMonitor = new UdpStreamMonitor(udpStreamProcessor);
    }

    @Test
    public void testSetElapsedTimeRolloverCondition() {
        udpStreamMonitor.setElapsedTimeRolloverCondition(UdpStreamMonitor.ELAPSED_TIME_MIN);
        verify(udpStreamProcessor).setElapsedTimeRolloverCondition(UdpStreamMonitor.ELAPSED_TIME_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void testSetElapsedTimeRolloverConditionNullArg() {
        udpStreamMonitor.setElapsedTimeRolloverCondition(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetElapsedTimeRolloverConditionBelowRangeArg() {
        udpStreamMonitor.setElapsedTimeRolloverCondition(UdpStreamMonitor.ELAPSED_TIME_MIN - 10);
    }

    @Test
    public void testSetByteCountRolloverCondition() {
        udpStreamMonitor.setByteCountRolloverCondition(UdpStreamMonitor.BYTE_COUNT_MIN);
        verify(udpStreamProcessor).setByteCountRolloverCondition(UdpStreamMonitor.BYTE_COUNT_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void testSetByteCountRolloverConditionNullArg() {
        udpStreamMonitor.setByteCountRolloverCondition(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetByteCountRolloverConditionBelowRangeArg() {
        udpStreamMonitor.setByteCountRolloverCondition(UdpStreamMonitor.BYTE_COUNT_MIN - 10);
    }

    @Test
    public void testMonitoredPort() {
        udpStreamMonitor.setMonitoredPort(UdpStreamMonitor.MONITORED_PORT_MIN);
        assertThat(udpStreamMonitor.getMonitoredPort(), is(UdpStreamMonitor.MONITORED_PORT_MIN));
    }

    @Test(expected = NullPointerException.class)
    public void testMonitoredPortNullArg() {
        udpStreamMonitor.setMonitoredPort(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMonitoredPortBelowRangeArg() {
        udpStreamMonitor.setMonitoredPort(UdpStreamMonitor.MONITORED_PORT_MIN - 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMonitoredPortAboveRangeArg() {
        udpStreamMonitor.setMonitoredPort(UdpStreamMonitor.MONITORED_PORT_MAX + 10);
    }

    @Test
    public void testMonitoredAddress() {
        String addr = "127.0.0.1";
        udpStreamMonitor.setMonitoredAddress(addr);
        assertThat(udpStreamMonitor.getMonitoredAddress(), is(addr));
    }

    @Test(expected = NullPointerException.class)
    public void testMonitoredAddressNullArg() {
        udpStreamMonitor.setMonitoredAddress(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMonitoredAddressUnresolvableArg() {
        udpStreamMonitor.setMonitoredAddress("127.0.0.0.1");
    }

    @Test
    public void testSetCatalogFramework() {
        CatalogFramework catalogFramework = mock(CatalogFramework.class);
        udpStreamMonitor.setCatalogFramework(catalogFramework);
        verify(udpStreamProcessor).setCatalogFramework(catalogFramework);
    }

    @Test(expected = NullPointerException.class)
    public void testSetCatalogFrameworkNullArg() {
        udpStreamMonitor.setCatalogFramework(null);
    }

    @Test
    public void testSetMetacardTypeList() {
        List<MetacardType> metacardTypeList = Collections.emptyList();
        udpStreamMonitor.setMetacardTypeList(metacardTypeList);
        verify(udpStreamProcessor).setMetacardTypeList(metacardTypeList);
    }

    @Test(expected = NullPointerException.class)
    public void testSetMetacardTypeListNullArg() {
        udpStreamMonitor.setMetacardTypeList(null);
    }

    @Test
    public void testSetTitle() {
        String title = "title";
        udpStreamMonitor.setParentTitle(title);
        assertThat(udpStreamMonitor.getTitle()
                .get(), is(title));
    }

    @Test
    public void testGetStreamUri() {
        String addr = "127.0.0.1";
        int port = 1000;
        udpStreamMonitor.setMonitoredAddress(addr);
        udpStreamMonitor.setMonitoredPort(port);
        assertThat(udpStreamMonitor.getStreamUri()
                .get()
                .toString(), is("udp://" + addr + ":" + port));
    }

    @Test(expected = NullPointerException.class)
    public void testSetRolloverConditionNullArg() {
        udpStreamMonitor.setRolloverCondition(null);
    }

    @Test
    public void testSetRolloverCondition() {
        RolloverCondition rolloverCondition = mock(RolloverCondition.class);
        udpStreamMonitor.setRolloverCondition(rolloverCondition);
        verify(udpStreamProcessor).setRolloverCondition(rolloverCondition);
    }

    @Test(expected = NullPointerException.class)
    public void testSetFilenameTemplateNullArg() {
        udpStreamMonitor.setFilenameTemplate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetFilenameTemplateBlankArg() {
        udpStreamMonitor.setFilenameTemplate("");
    }

    @Test
    public void testSetFilenameTemplate() {
        String filenameTemplate = "template";
        udpStreamMonitor.setFilenameTemplate(filenameTemplate);
        verify(udpStreamProcessor).setFilenameTemplate(filenameTemplate);
    }

    @Test(expected = NullPointerException.class)
    public void testSetFilenameGeneratorNullArg() {
        udpStreamMonitor.setFilenameGenerator(null);
    }

    @Test
    public void testSetFilenameGenerator() {
        FilenameGenerator filenameGenerator = mock(FilenameGenerator.class);
        udpStreamMonitor.setFilenameGenerator(filenameGenerator);
        verify(udpStreamProcessor).setFilenameGenerator(filenameGenerator);
    }

    @Test(expected = NullPointerException.class)
    public void testSetStanag4609ProcessorNullArg() {
        udpStreamMonitor.setStanag4609Processor(null);
    }

    @Test
    public void testSetStanag4609Processor() {
        Stanag4609Processor stanag4609Processor = mock(Stanag4609Processor.class);
        udpStreamMonitor.setStanag4609Processor(stanag4609Processor);
        verify(udpStreamProcessor).setStanag4609Processor(stanag4609Processor);
    }

    @Test(expected = NullPointerException.class)
    public void testSetDefaultKlvHandlerNullArg() {
        udpStreamMonitor.setDefaultKlvHandler(null);
    }

    @Test
    public void testSetDefaultKlvHandler() {
        KlvHandler klvHandler = mock(KlvHandler.class);
        udpStreamMonitor.setDefaultKlvHandler(klvHandler);
        verify(udpStreamProcessor).setDefaultKlvHandler(klvHandler);
    }

    @Test(expected = NullPointerException.class)
    public void testSetKlvLocationSubsampleCountNullArg() {
        udpStreamMonitor.setKlvLocationSubsampleCount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetKlvLocationSubsampleCountBelowRangelArg() {
        udpStreamMonitor.setKlvLocationSubsampleCount(UdpStreamMonitor.SUBSAMPLE_COUNT_MIN - 10);
    }

    @Test
    public void testSetKlvLocationSubsampleCount() {
        udpStreamMonitor.setKlvLocationSubsampleCount(UdpStreamMonitor.SUBSAMPLE_COUNT_MIN);
        verify(udpStreamProcessor).setKlvLocationSubsampleCount(UdpStreamMonitor.SUBSAMPLE_COUNT_MIN);
    }

    @Test(expected = NullPointerException.class)
    public void testSetKlvProcessorNullArg() {
        udpStreamMonitor.setKlvProcessor(null);
    }

    @Test
    public void testSetKlvProcessor() {
        KlvProcessor klvProcessor = mock(KlvProcessor.class);
        udpStreamMonitor.setKlvProcessor(klvProcessor);
        verify(udpStreamProcessor).setKlvProcessor(klvProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void testSetKlvHandlerFactoryNullArg() {
        udpStreamMonitor.setKlvHandlerFactory(null);
    }

    @Test
    public void testSetKlvHandlerFactory() {
        KlvHandlerFactory klvHandlerFactory = mock(KlvHandlerFactory.class);
        udpStreamMonitor.setKlvHandlerFactory(klvHandlerFactory);
        verify(udpStreamProcessor).setKlvHandlerFactory(klvHandlerFactory);
    }

}
