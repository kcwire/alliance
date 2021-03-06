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
package org.codice.alliance.video.stream.mpegts.netty;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.util.List;

import org.codice.alliance.libs.stanag4609.DecodedKLVMetadataPacket;
import org.jcodec.codecs.h264.io.model.NALUnit;
import org.jcodec.containers.mps.MTSUtils;
import org.junit.Before;
import org.junit.Test;

import io.netty.channel.embedded.EmbeddedChannel;

public class TestPESPacketToApplicationDataDecoder {

    private static final byte[] EMPTY_ARRAY = new byte[] {};

    private static final ByteBuffer EMPTY_BUF = ByteBuffer.wrap(EMPTY_ARRAY);

    private PESPacketToApplicationDataDecoder decoder;

    private PESPacket pesPacket;

    @Before
    public void setup() {
        decoder = new PESPacketToApplicationDataDecoder(true);
        pesPacket = mock(PESPacket.class);
    }

    @Test
    public void testDecodeNALUnits() throws Exception {

        when(pesPacket.getStreamType()).thenReturn(MTSUtils.StreamType.VIDEO_H264);
        when(pesPacket.getPayload()).thenReturn(EMPTY_ARRAY);

        PESPacketToApplicationDataDecoder.NALReader nalReader = mock(
                PESPacketToApplicationDataDecoder.NALReader.class);
        when(nalReader.next(any())).thenReturn(EMPTY_BUF)
                .thenReturn(EMPTY_BUF)
                .thenReturn(null);
        decoder.setNalReader(nalReader);

        PESPacketToApplicationDataDecoder.NALParser nalParser = mock(
                PESPacketToApplicationDataDecoder.NALParser.class);

        NALUnit nalUnit1 = mock(NALUnit.class);
        NALUnit nalUnit2 = mock(NALUnit.class);

        when(nalParser.parse(any())).thenReturn(nalUnit1)
                .thenReturn(nalUnit2);

        decoder.setNalParser(nalParser);

        EmbeddedChannel channel = new EmbeddedChannel(decoder);

        channel.writeInbound(pesPacket);

        List<Object> outputList = NettyUtility.read(channel);

        assertThat(outputList, hasSize(1));
        assertThat(outputList.get(0), is(instanceOf(DecodedStreamData.class)));
        DecodedStreamData decodedStreamData = (DecodedStreamData) outputList.get(0);
        assertThat(decodedStreamData.getDecodedKLVMetadataPacket()
                .isPresent(), is(false));
        assertThat(decodedStreamData.getNalUnits()
                .isPresent(), is(true));
        assertThat(decodedStreamData.getNalUnits()
                .get(), hasSize(2));
        assertThat(decodedStreamData.getNalUnits()
                .get()
                .get(0), is(nalUnit1));
        assertThat(decodedStreamData.getNalUnits()
                .get()
                .get(1), is(nalUnit2));
    }

    @Test
    public void testKlvMetadataPrivateData() throws Exception {

        when(pesPacket.getStreamType()).thenReturn(MTSUtils.StreamType.PRIVATE_DATA);
        when(pesPacket.getPacketId()).thenReturn(1);
        when(pesPacket.getPayload()).thenReturn(EMPTY_ARRAY);

        PESPacketToApplicationDataDecoder.KlvParser klvParser = mock(
                PESPacketToApplicationDataDecoder.KlvParser.class);
        DecodedKLVMetadataPacket decodedKLVMetadataPacket = mock(DecodedKLVMetadataPacket.class);
        when(klvParser.parse(eq(EMPTY_ARRAY), any())).thenReturn(decodedKLVMetadataPacket);

        decoder.setKlvParser(klvParser);

        EmbeddedChannel channel = new EmbeddedChannel(decoder);

        channel.writeInbound(pesPacket);

        List<Object> outputList = NettyUtility.read(channel);

        assertThat(outputList, hasSize(1));
        assertThat(outputList.get(0), is(instanceOf(DecodedStreamData.class)));
        DecodedStreamData decodedStreamData = (DecodedStreamData) outputList.get(0);
        assertThat(decodedStreamData.getDecodedKLVMetadataPacket()
                .isPresent(), is(true));
        assertThat(decodedStreamData.getNalUnits()
                .isPresent(), is(false));
        assertThat(decodedStreamData.getPacketId(), is(1));
        assertThat(decodedStreamData.getDecodedKLVMetadataPacket()
                .get(), is(decodedKLVMetadataPacket));

    }

    @Test
    public void testKlvMetadataMetaPes() throws Exception {

        when(pesPacket.getStreamType()).thenReturn(MTSUtils.StreamType.META_PES);
        when(pesPacket.getPacketId()).thenReturn(1);
        when(pesPacket.getPayload()).thenReturn(EMPTY_ARRAY);

        PESPacketToApplicationDataDecoder.KlvParser klvParser = mock(
                PESPacketToApplicationDataDecoder.KlvParser.class);
        DecodedKLVMetadataPacket decodedKLVMetadataPacket = mock(DecodedKLVMetadataPacket.class);
        when(klvParser.parse(eq(EMPTY_ARRAY), any())).thenReturn(decodedKLVMetadataPacket);

        decoder.setKlvParser(klvParser);

        EmbeddedChannel channel = new EmbeddedChannel(decoder);

        channel.writeInbound(pesPacket);

        List<Object> outputList = NettyUtility.read(channel);

        assertThat(outputList, hasSize(1));
        assertThat(outputList.get(0), is(instanceOf(DecodedStreamData.class)));
        DecodedStreamData decodedStreamData = (DecodedStreamData) outputList.get(0);
        assertThat(decodedStreamData.getDecodedKLVMetadataPacket()
                .isPresent(), is(true));
        assertThat(decodedStreamData.getNalUnits()
                .isPresent(), is(false));
        assertThat(decodedStreamData.getPacketId(), is(1));
        assertThat(decodedStreamData.getDecodedKLVMetadataPacket()
                .get(), is(decodedKLVMetadataPacket));

    }

}
