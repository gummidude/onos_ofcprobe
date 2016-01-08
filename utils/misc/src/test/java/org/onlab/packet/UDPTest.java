/*
 * Copyright 2014-2015 Open Networking Laboratory
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



package org.onlab.packet;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for class {@link UDP}.
 */
public class UDPTest {
    private static final byte[] IPV4_SOURCE_ADDRESS = {
            (byte) 192, (byte) 168, (byte) 1, (byte) 1
    };
    private static final byte[] IPV4_DESTINATION_ADDRESS = {
            (byte) 192, (byte) 168, (byte) 1, (byte) 2
    };
    private static final byte[] IPV6_SOURCE_ADDRESS = {
            (byte) 0xfe, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01
    };
    private static final byte[] IPV6_DESTINATION_ADDRESS = {
            (byte) 0xfe, (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02
    };

    private static IPv4 ipv4 = new IPv4();
    private static IPv6 ipv6 = new IPv6();
    private static byte[] bytePacketUDP4 = {
            (byte) 0x00, (byte) 0x50, // src port
            (byte) 0x00, (byte) 0x60, // dst port
            (byte) 0x00, (byte) 0x08, // length
            (byte) 0x7b, (byte) 0xda, // checksum
    };
    private static byte[] bytePacketUDP6 = {
            (byte) 0x00, (byte) 0x50, // src port
            (byte) 0x00, (byte) 0x60, // dst port
            (byte) 0x00, (byte) 0x08, // length
            (byte) 0x02, (byte) 0x2a, // checksum
    };

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ipv4.setSourceAddress(IPv4.toIPv4Address(IPV4_SOURCE_ADDRESS));
        ipv4.setDestinationAddress(IPv4.toIPv4Address(IPV4_DESTINATION_ADDRESS));
        ipv4.setProtocol(IPv4.PROTOCOL_UDP);

        ipv6.setSourceAddress(IPV6_SOURCE_ADDRESS);
        ipv6.setDestinationAddress(IPV6_DESTINATION_ADDRESS);
        ipv6.setNextHeader(IPv6.PROTOCOL_UDP);
    }

    /**
     * Tests serialize and setters.
     */
    @Test
    public void testSerialize() {
        UDP udp = new UDP();
        udp.setSourcePort((short) 0x50);
        udp.setDestinationPort((short) 0x60);

        udp.setParent(ipv4);
        assertArrayEquals(bytePacketUDP4, udp.serialize());
        udp.resetChecksum();
        udp.setParent(ipv6);
        assertArrayEquals(bytePacketUDP6, udp.serialize());
    }

    /**
     * Tests deserialize and getters.
     */
    @Test
    public void testDeserialize() {
        UDP udp = new UDP();
        udp.deserialize(bytePacketUDP4, 0, bytePacketUDP4.length);

        assertThat(udp.getSourcePort(), is((short) 0x50));
        assertThat(udp.getDestinationPort(), is((short) 0x60));
        assertThat(udp.getLength(), is((short) 8));
        assertThat(udp.getChecksum(), is((short) 0x7bda));
    }

    /**
     * Tests comparator.
     */
    @Test
    public void testEqual() {
        UDP udp1 = new UDP();
        udp1.setSourcePort((short) 0x50);
        udp1.setDestinationPort((short) 0x60);

        UDP udp2 = new UDP();
        udp2.setSourcePort((short) 0x70);
        udp2.setDestinationPort((short) 0x60);

        assertTrue(udp1.equals(udp1));
        assertFalse(udp1.equals(udp2));
    }
}
