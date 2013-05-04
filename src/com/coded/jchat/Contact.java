package com.coded.jchat;
/*
 * Copyright (C) 2006 Hassen Ben Tanfous
 * All right reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 	1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 	2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 	3. Neither the name of the Hassen Ben Tanfous nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * Contact.java
 * représente un contact avec son ip, son port, et son pseudo
 * Date: 31/12/2005
 * @author Hassen Ben Tanfous
 */

public class Contact {
    private String ip, port, pseudo;
    private int iport;

    public Contact (String ip, String port, String pseudo) {
        this.ip = ip;
        this.port = port;
        this.pseudo = pseudo;
        iport = Integer.parseInt (port);
    }

    public Contact (String ip, int port, String pseudo) {
        this (ip, Integer.toString(port), pseudo);
    }

    public void setIP (String ip) {
        this.ip =ip;
    }

    public String getIP () {
        return this.ip;
    }

    public void setPort (String port) {
        this.port = port;
        this.iport = Integer.parseInt (port);
    }

    public void setPort (int port) {
        setPort (Integer.toString (port));
    }

    public String getPort () {
        return this.port;
    }

    public int getPortInt() {
        return iport;
    }

    public void setPseudo (String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo () {
        return pseudo;
    }
}
