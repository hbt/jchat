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
 * JChat.java
 * permet de démarrer une conversation chat avec un ou plusieurs contacts
 * permet de démarrer une conversation VOIP avec 1 contact
 * permet d'envoyer 1 fichier à la fois au contact de votre choix
 * permet de gérer vos contacts (ajouter, modifier, delete, importer, exporter) dans
 * les listes online et offline
 * Un serveur n'est pas nécessaire, il vous suffit de communiquer votre IP et le port
 * de votre serveur pour entrer en contact avec quelqu'un
 *
 * Développé spécialement pour les réseaux privés
 *
 * Certaines parties du code sont en commentaires parce que JChat fait parti
 * de la suite d'outils JINetWork (JInet, JWhois, JMail, JFtpClient etc.)
 *
 * @version 1.0
 * Date: 31/12/2005
 * @author Hassen Ben Tanfous
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.net.*;
import java.text.*;

import com.coded.jchat.msg.*;
import javax.sound.sampled.*;

public class JChat {

    /** variable TAMPON pour le transfert de fichiers */
    public static final int BUFFER = 4096;

    /** nom du fichier où se retrouveront les contacts */
    public static final String FICHIER_CONTACTS = "listeContacts.txt";

    //Menu
    private JMenuBar bar;
    private JMenu
            menuConnect;
//            menuInformations,
//            menuInfoReseau;

    private JMenuItem
            itemConnectServer,
            itemConnectClose;
//            itemReseauLocalhost,
//            itemReseauParNom,
//            itemInfoWhois,
//            itemInfoNewWhois;

    private JButton
            boutonAjouter,
            boutonRetirer,
            boutonModifier, //inutilisé dans JChat
            boutonImporter,
            boutonExporter,
//            boutonEmail,
//            boutonURL,
            boutonConnectServer,
            boutonContactAjouter; //frame contact

    private JList
            listeOnline,
            listeOffline;

    private DefaultListModel

            listeOnlineIP,
            listeOnlinePseudo,
            listeOfflineIP,
            listeOfflinePseudo,

            listeContactsIP,
            listeContactsPort,
            listeContactsPseudo;

    private JLabel

            lblPseudo,
            lblOnline,
            lblOffline,

            lblConnectHostname,
            lblConnectIP,
            lblConnectPort,

            lblContactAjouterIP,
            lblContactAjouterPort,
            lblContactAjouterPseudo;

    private JScrollPane
            scrollListeOnline,
            scrollListeOffline;

    private JTextField
            txtPseudo,
            txtConnectHostname,
            txtConnectIP,

            txtConnectPort,
            txtContactAjouterIP,
            txtContactAjouterPort,
            txtContactAjouterPseudo;

    private boolean
            boolOnlineSelected,
            boolOfflineSelected,
            boolAccept;

    //Net
    private ServerSocket server;

    //Containers
    private JFrame
            frameServer,
            frameConnectServer,
            frameContactAjouter;

    private Container
            cServer,
            cConnectServer,
            cContactAjouter;

    private JFileChooser
            jfcImporter,
            jfcExporter;

    private BufferedReader lireLecture; //permet de lire les données d'une Socket client

    private GererClient gerant; //permet de gérer tous les clients qui se connectent au serveur
    private Socket nouveau; //utilisé pour le transfert de fichier

    private DecimalFormat formatPourcent; //affichage du pourcentage lors d'un transfert de fichier

    private Messagerie msg;

    private boolean boolStopTalking; //lorsque le client arretera la conversation VOIP

    private boolean boolVOIP; //lorsque le client démarre la conversation VOIP

    public JChat() {
        instancierComposants();
        configurerComposants();
    }

    /**
     * permet d'ajouter un composant "comp" au container "c"
     * @param c Container
     * @param comp Component
     * @param x int
     * @param y int
     * @param x2 int
     * @param y2 int
     */
    private void ajouterComposant(Container c, Component comp, int x, int y,
                                  int x2, int y2) {
        comp.setBounds(x, y, x2, y2);
        c.add(comp);
    }

    private void instancierComposants() {
        //Containers
        frameServer = new JFrame("JChat par Hassen Ben Tanfous");
        cServer = frameServer.getContentPane();

        frameConnectServer = new JFrame("Configuration");
        cConnectServer = frameConnectServer.getContentPane();

        frameContactAjouter = new JFrame("Ajouter");
        cContactAjouter = frameContactAjouter.getContentPane();

        //Menu
        //JMenuBar
        bar = new JMenuBar();

        //Menus
        menuConnect = new JMenu("Connect");
//        menuInformations = new JMenu("Informations");
//        menuInfoReseau = new JMenu("Réseau");

        //MenuItems
        itemConnectServer = new JMenuItem("Start server");
        itemConnectClose = new JMenuItem("Close server");

        //informations
//        itemReseauLocalhost = new JMenuItem("Localhost infos");
//        itemReseauParNom = new JMenuItem("Par nom");
//        itemInfoWhois = new JMenuItem("WHOIS");
//        itemInfoNewWhois = new JMenuItem("Add new WHOIS server");

        //boutons
        boutonAjouter = new JButton("Add");
        boutonRetirer = new JButton("Delete");

        boutonModifier = new JButton("Modifier");

        boutonImporter = new JButton("Importer");
        boutonExporter = new JButton("Exporter");
//        boutonEmail = new JButton("Composer mail");
//        boutonURL = new JButton("Direct dld/upld");

        //boutons Server Connect
        boutonConnectServer = new JButton("Start");

        //boutons Contact Ajouter
        boutonContactAjouter = new JButton("Add");

        //modeleListe
        listeOnlineIP = new DefaultListModel();
        listeOnlinePseudo = new DefaultListModel();

        listeOfflineIP = new DefaultListModel();
        listeOfflinePseudo = new DefaultListModel();

        listeContactsIP = new DefaultListModel();
        listeContactsPort = new DefaultListModel();
        listeContactsPseudo = new DefaultListModel();

        //liste
        listeOnline = new JList(listeOnlinePseudo);
        listeOffline = new JList(listeOfflinePseudo);

        //textfield
        txtPseudo = new JTextField();
        txtConnectHostname = new JTextField();

        txtConnectIP = new JTextField();
        txtConnectPort = new JTextField();

        txtContactAjouterIP = new JTextField();
        txtContactAjouterPort = new JTextField();
        txtContactAjouterPseudo = new JTextField();

        //Label
        lblOnline = new JLabel("Online contacts");
        lblOffline = new JLabel("Offline contacts");

        //Server connect label
        lblConnectHostname = new JLabel("Hostname:");
        lblConnectIP = new JLabel("IP:");

        lblConnectPort = new JLabel("Port:");
        lblContactAjouterIP = new JLabel("IP:");

        lblContactAjouterPort = new JLabel("Port:");
        lblContactAjouterPseudo = new JLabel("Pseudo:");

        lblPseudo = new JLabel("Pseudo:");

        //scroll
        scrollListeOnline = new JScrollPane(listeOnline);
        scrollListeOffline = new JScrollPane(listeOffline);

        //JFileChooser
        jfcImporter = new JFileChooser();
        jfcExporter = new JFileChooser();

        //DecimalFormat
        formatPourcent = new DecimalFormat("0.00");
    }

    private void configurerComposants() {
        txtPseudo.setText("localhost8080"); //pseudo par défaut
        msg.titre = "JChat par Hassen Ben Tanfous"; //titre de tous les messages

        //Écouteurs boutons
        //ConnectServer
        boutonConnectServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == boutonConnectServer) {
                    if (txtConnectIP.getText().equals("")) { //aucun IP
                        msg.msge("Vous devez entrer un IP");
                    } else if (txtConnectPort.getText().equals("")) { //aucun Port
                        msg.msge("Vous devez entrer un port");
                    } else {
                        try {
                            int port = Integer.parseInt(txtConnectPort.getText());
                            //démarre le serveur
                            server = new ServerSocket(port);
                            msg.msgi(
                                    "Serveur démarré avec succès sur la machine " +
                                    txtConnectHostname.getText() +
                                    "\nCommuniquez votre ip " +
                                    txtConnectIP.getText() + " et votre port " +
                                    txtConnectPort.getText() +
                                    " à vos contacts");

                            boolAccept = true; //commence à accepter les clients

                            boutonImporter.setEnabled(true);
                            boutonExporter.setEnabled(true);
                            boutonModifier.setEnabled(true);
                            boutonAjouter.setEnabled(true);
                            boutonRetirer.setEnabled(true);

                            //attend la connection d'un client
                            gerant = new GererClient();

                            //importe la liste des contacts
                            importerDefaultListe(null);

                            for (int i = 0; i < listeContactsIP.size(); i++) {
                                new QuestionConnectedContacts(listeContactsIP.
                                        get(i),
                                        listeContactsPort.get(i),
                                        listeContactsPseudo.get(i));
                            }
                            frameConnectServer.setVisible(false);

                        } catch (NumberFormatException ex) {
                            msg.msge("Votre port doit être un chiffre");
                        } catch (IOException ex) {
                            msg.msge("Impossible de démarrer le serveur\n" +
                                     "Veuillez fermer le port en cours d'utilisation");
                            frameConnectServer.setVisible(false);
                        }
                    }
                }
            }
        });

        //gestion des contacts ajouter, retirer, importer, exporter
        boutonAjouter.addActionListener(alContacts);
        boutonRetirer.addActionListener(alContacts);
        boutonImporter.addActionListener(alContacts);
        boutonExporter.addActionListener(alContacts);

        boutonContactAjouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == boutonContactAjouter) {
                    if (txtContactAjouterIP.getText().equals("")) { //aucun IP
                        msg.msge(
                                "Vous devez entrer l'adresse IP de votre contact");
                    } else if (txtContactAjouterPort.getText().equals("")) { //aucun Port
                        msg.msge("Vous devez entrer le port de votre contact");
                    } else if (txtContactAjouterPseudo.getText().equals("")) { //aucun Pseudo
                        txtContactAjouterPseudo.setText(txtContactAjouterIP.
                                getText());
                    } else {
                        try {
                            int port = Integer.parseInt(txtContactAjouterPort.
                                    getText());

                            //vérifie si le contact est online ou offline
                            new QuestionConnectedContacts(txtContactAjouterIP.
                                    getText(),
                                    txtContactAjouterPort.getText(),
                                    txtContactAjouterPseudo.getText());

                            frameContactAjouter.setVisible(false);

                            txtContactAjouterIP.setText("");
                            txtContactAjouterPort.setText("");
                            txtContactAjouterPseudo.setText("");

                        } catch (NumberFormatException ex) {
                            msg.msge("Port invalide");
                        }
                    }
                }
            }
        });

        //Écouteurs items
        itemConnectServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frameConnectServer.setVisible(true);
                try {
                    if (e.getSource() == itemConnectServer) {
                        //adresse IP du réseau
                        InetAddress inetServer = InetAddress.getLocalHost();
                        txtConnectHostname.setText(inetServer.
                                getCanonicalHostName());
                        txtConnectIP.setText(inetServer.getHostAddress());
                        txtConnectPort.setText("8080");
                    }
                } catch (UnknownHostException ex) {
                    msg.msge("Impossible de déterminer votre host");
                }
            }
        });

        itemConnectClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == itemConnectClose) {
                    try {
                        //préviens tous vos contacts que vous allez fermer votre serveur
                        for (int i = 0; i < listeOnlineIP.size(); i++) {
                            new ReponseOffline(listeOnlineIP.get(i),
                                               listeContactsPort.get(
                                    listeContactsIP.indexOf(listeOnlineIP.get(i))));
                        }

                        //enregistre vos contacts
                        exporterDefaultListe(null);

                        //effacement et fermeture
                        listeContactsIP.removeAllElements();
                        listeContactsPseudo.removeAllElements();
                        listeContactsPort.removeAllElements();
                        listeOfflineIP.removeAllElements();
                        listeOnlineIP.removeAllElements();
                        listeOnlinePseudo.removeAllElements();
                        listeOfflinePseudo.removeAllElements();

                        boutonImporter.setEnabled(false);
                        boutonExporter.setEnabled(false);
                        boutonModifier.setEnabled(false);
                        boutonAjouter.setEnabled(false);
                        boutonRetirer.setEnabled(false);

                        server.close();
                        msg.msgi("Serveur sur " + txtConnectHostname.getText() +
                                 " est fermé\n" +
                                 "Le port " + txtConnectPort.getText() +
                                 " est libre");

                    } catch (Exception ex) {
                        msg.msge("Impossible de fermer le serveur sur " +
                                 txtConnectHostname.getText());
                    }
                }
            }
        });

        //Écouteur liste
        listeOnline.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                boolOnlineSelected = true;
                boolOfflineSelected = false;

                //démarre une conversation lors d'un double click
                if (e.getClickCount() == 2) {
                    new BuildClient(listeOnlineIP.get(
                            listeOnline.
                            getSelectedIndex()), listeContactsPort.get(
                                    listeContactsIP.indexOf(listeOnlineIP.get(
                                            listeOnline.
                                            getSelectedIndex()))),
                                    listeOnlinePseudo.get(listeOnline.
                            getSelectedIndex()));
                }
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        listeOffline.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                boolOnlineSelected = false;
                boolOfflineSelected = true;
            }

            public void mouseReleased(MouseEvent e) {
            }
        });

        //Modèle JFileChooser
        //importer
        jfcImporter.setApproveButtonText("Load");
        jfcImporter.setApproveButtonToolTipText(
                "Importe la liste de vos contacts");

        jfcImporter.setDialogTitle("Load contact list");
        jfcImporter.setMultiSelectionEnabled(false);
        jfcImporter.setAcceptAllFileFilterUsed(true);

        jfcImporter.addChoosableFileFilter(new Filtres(".txt", "Text File"));

        //exporter
        jfcExporter.setApproveButtonText("Save");
        jfcExporter.setApproveButtonToolTipText("Exporte la liste de tous vos " +
                                                "contacts");

        jfcExporter.setDialogTitle("Save contact list");
        jfcExporter.setMultiSelectionEnabled(false);
        jfcExporter.setAcceptAllFileFilterUsed(true);

        jfcExporter.addChoosableFileFilter(new Filtres(".txt", "Text File"));

        //Modèle JTextField
        //connectServer
        txtConnectHostname.setEditable(false);
        txtConnectHostname.setToolTipText("le nom de votre ordinateur");
        txtConnectIP.setToolTipText("IP à communiquer à vos contacts");
        txtConnectPort.setToolTipText("Port pour votre server");

        //Modèle bouton
        //gestion contacts
        boutonAjouter.setToolTipText("Ajoute un contact à votre liste");
        boutonRetirer.setToolTipText("Retire un contact de votre liste");
        boutonImporter.setToolTipText("Importe une liste de contacts");
        boutonExporter.setToolTipText("Exporte la liste de contacts");
        boutonModifier.setToolTipText("Modifie le contact sélectionné");
//        boutonEmail.setToolTipText("Démarre le compositeur d'email");
//        boutonURL.setToolTipText("Direct download/upload");
//        boutonURL.setBackground(Color.red);
//        boutonEmail.setBackground(Color.RED);

        boutonImporter.setEnabled(false);
        boutonExporter.setEnabled(false);

        boutonModifier.setEnabled(false);
        boutonAjouter.setEnabled(false);

        boutonRetirer.setEnabled(false);

        //Modèle item
        itemConnectServer.setToolTipText("Démarre un server");
        itemConnectClose.setToolTipText(
                "Ferme le server en cours et libère le port");

//        itemReseauLocalhost.setToolTipText("Informations sur le localhost");
//        itemReseauParNom.setToolTipText(
//                "Informations sur le nom de la machine entrée");
//        itemInfoWhois.setToolTipText("Informations sur une adresse");
//        itemInfoNewWhois.setToolTipText("Permet d'ajouter un nouveau whois");

        //menu Connect
        menuConnect.add(itemConnectServer);
        menuConnect.addSeparator();
        menuConnect.add(itemConnectClose);

        //menu informations
//        menuInfoReseau.add(itemReseauLocalhost);
//        menuInfoReseau.addSeparator();
//        menuInfoReseau.add(itemReseauParNom);
//
//        menuInformations.add(menuInfoReseau);
//        menuInformations.addSeparator();
//        menuInformations.add(itemInfoWhois);
//        menuInformations.add(itemInfoNewWhois);

        //Menu
        bar.add(menuConnect);
//        bar.add(menuInformations);

        //Ajouter les composants au container cServer
        cServer.setLayout(null);
        ajouterComposant(cServer, bar, 0, 10, 300, 20);
        ajouterComposant(cServer, lblPseudo, 0, 50, 100, 20);
        ajouterComposant(cServer, txtPseudo, 65, 52, 100, 20);

        ajouterComposant(cServer, lblOnline, 50, 110, 150, 20);
        ajouterComposant(cServer, scrollListeOnline, 50, 140, 150, 350);

        ajouterComposant(cServer, boutonAjouter, 225, 180, 100, 20);
        ajouterComposant(cServer, boutonRetirer, 225, 210, 100, 20);
        ajouterComposant(cServer, boutonModifier, 225, 240, 100, 20);
        ajouterComposant(cServer, boutonImporter, 225, 300, 100, 20);
        ajouterComposant(cServer, boutonExporter, 225, 330, 100, 20);

        ajouterComposant(cServer, lblOffline, 350, 110, 150, 20);
        ajouterComposant(cServer, scrollListeOffline, 350, 140, 150, 350);

//        ajouterComposant(cServer, boutonURL, 550, 50, 175, 20);
//        ajouterComposant(cServer, boutonEmail, 550, 80, 175, 20);

        frameServer.setLocation(10, 10);
        frameServer.setSize(800, 600);
        frameServer.setVisible(true);
        frameServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //ajouter les composants au container cConnectServer
        cConnectServer.setLayout(null);
        ajouterComposant(cConnectServer, lblConnectHostname, 0, 0, 100, 20);
        ajouterComposant(cConnectServer, txtConnectHostname, 75, 2, 100, 20);
        ajouterComposant(cConnectServer, lblConnectIP, 0, 25, 100, 20);
        ajouterComposant(cConnectServer, txtConnectIP, 75, 27, 100, 20);
        ajouterComposant(cConnectServer, lblConnectPort, 0, 50, 100, 20);
        ajouterComposant(cConnectServer, txtConnectPort, 75, 52, 100, 20);
        ajouterComposant(cConnectServer, boutonConnectServer, 50, 100, 100, 20);

        frameConnectServer.setLocation(20, 20);
        frameConnectServer.setSize(200, 200);

        //ajouter les composants au container cContactAjouter
        cContactAjouter.setLayout(null);
        ajouterComposant(cContactAjouter, lblContactAjouterIP, 0, 0, 100, 20);
        ajouterComposant(cContactAjouter, txtContactAjouterIP, 75, 2, 100, 20);
        ajouterComposant(cContactAjouter, lblContactAjouterPort, 0, 25, 100, 20);
        ajouterComposant(cContactAjouter, txtContactAjouterPort, 75, 27, 50, 20);
        ajouterComposant(cContactAjouter, lblContactAjouterPseudo, 0, 50, 100,
                         20);
        ajouterComposant(cContactAjouter, txtContactAjouterPseudo, 75, 52, 100,
                         20);
        ajouterComposant(cContactAjouter, boutonContactAjouter, 50, 100, 100,
                         20);

        frameContactAjouter.setLocation(300, 200);
        frameContactAjouter.setSize(200, 200);

    }

    /**
     * exporte la liste finale de contacts et gère les entrées des boutons
     * boutonExporter
     */
    public void exporterDefaultListe(String file) {
        if (file == null) {
            file = FICHIER_CONTACTS;
        }
        try {
            BufferedWriter ecrire = new BufferedWriter(new FileWriter(
                    file));

            for (int i = 0; i < listeContactsIP.size(); i++) {
                ecrire.write(listeContactsIP.getElementAt(i).toString() + ":" +
                             listeContactsPort.getElementAt(i).toString() + ":" +
                             listeContactsPseudo.getElementAt(i) + "\n");
            }
            ecrire.close();

        } catch (IOException ex) {
            msg.msge("Impossible d'écrire la liste de contacts " + file);
        }
    }

    /**
     * importe la liste de contacts par défaut et gère les entrées des boutons
     * boutonImporter
     */
    public void importerDefaultListe(String file) {
        String
                lecture,
                ip,
                port,
                pseudo;
        if (file == null) {
            file = FICHIER_CONTACTS;
        }

        try {
            BufferedReader lire = new BufferedReader(new FileReader(
                    file));
            lecture = lire.readLine();

            while (lecture != null) {
                ip = lecture.substring(0, lecture.indexOf(":"));
                lecture = lecture.substring(lecture.indexOf(":") + 1);
                port = lecture.substring(0, lecture.indexOf(":"));
                lecture = lecture.substring(lecture.indexOf(":") + 1);
                pseudo = lecture.substring(0);
                listeContactsIP.addElement(ip);
                listeContactsPort.addElement(port);
                listeContactsPseudo.addElement(pseudo);
                lecture = lire.readLine();
            }

            lire.close();
        } catch (Exception ex) {
            msg.msge("Erreur durant l'import de la liste " + file +
                     "\nExporter une nouvelle liste ou déplacer le fichier " +
                     FICHIER_CONTACTS);
        }
    }


    /**
     * ActionListener gérant les évènements des boutons Ajouter, Retirer, Importer
     * Exporter et Modifier
     */
    private ActionListener alContacts = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            int choix;
            File f;

            if (e.getSource() == boutonAjouter) { //ajouter
                frameContactAjouter.setVisible(true);
            } else if (e.getSource() == boutonRetirer) { //retirer
                retirerContacts();
            } else if (e.getSource() == boutonImporter) { //importer
                choix = jfcImporter.showOpenDialog(null);
                if (choix == JFileChooser.APPROVE_OPTION) {
                    f = jfcImporter.getSelectedFile();
                    importerDefaultListe(f.toString());
                }
            } else if (e.getSource() == boutonExporter) { //exporter
                choix = jfcExporter.showSaveDialog(null);
                if (choix == JFileChooser.APPROVE_OPTION) {
                    f = jfcExporter.getSelectedFile();
                    exporterDefaultListe(f.toString());
                }
            }
        }
    };

    /**
     * permet de retirer les contacts sélectionnés dans une des deux listes
     */
    private void retirerContacts() {
        int value;
        int posi = -1;

        if (boolOnlineSelected) {
            value = listeOnline.getSelectedIndex();
            posi = listeContactsIP.indexOf(listeOnlineIP.get(value));
            listeOnlineIP.remove(value);
            listeOnlinePseudo.remove(value);
        } else if (boolOfflineSelected) {
            value = listeOffline.getSelectedIndex();
            posi = listeContactsIP.indexOf(listeOfflineIP.get(value));
            listeOfflineIP.remove(value);
            listeOfflinePseudo.remove(value);
        }
        if (posi != -1) {
            listeContactsIP.remove(posi);
            listeContactsPseudo.remove(posi);
            listeContactsPort.remove(posi);
        }
    }


    /**
     * permet d'ajouter des filtres au JFileChooser
     */
    private class Filtres extends javax.swing.filechooser.FileFilter {
        private String
                ext, //extension
                desp; //description

        /**
         * @param ext String
         * @param desp String
         */
        public Filtres(String ext, String desp) {
            this.ext = ext;
            this.desp = desp;
        }

        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else if (f.isFile() && f.getName().toLowerCase().endsWith(ext)) {
                return true;
            }
            return false;
        }

        public String getDescription() {
            return desp;
        }
    }


    /**
     * permet de construire les interfaces pour chat et gère les envoie de fichiers
     * et les conversations
     * démarre aussi les ClientVOIP
     */
    private class BuildClient extends Thread {
        private String
                ip,
                pseudo;

        private int port;

        private JFrame frameConv;
        private Container cConv;

        private JButton boutonSend, //send le fichier
                boutonAnnule, //annule le transfert de fichier
                boutonDemarrer, //démarrer conversation VOIP
                boutonArreter; //arrêter conversation VOIP

        private JTextField txt; //entrée de texte
        private JEditorPane editor;
        private JScrollPane scrollEditor;

        private JLabel
                lblTransfert1, //valeur en octets du taux de transfert
                lblTransfert2; //valeur en pourcentage du taux de transfert

        private JProgressBar progress;
        private JFileChooser jfc;

        private Socket client,
                clientFichierEnvoye, //socket d'envoie
                clientFichierRecu; //socket de réception

        private String
                contenu = "",
                          tailleFichier,
                          lecture, //lecture du flux de socket
                          filename; //nom du fichier qu'on tente d'envoyer

        private int choix; //choix dans le JFileChooser

        private File fichierRecu,
                fichierEnvoye;

        private BufferedReader lecteur;

        private VOIPClient clientVOIP;

        private PrintWriter printEcrire;

        /**
         * @param oip Object ip
         * @param oport Object port
         * @param opseudo Object pseudo
         */
        private BuildClient(Object oip, Object oport, Object opseudo) {
            ip = oip.toString();
            port = Integer.parseInt(oport.toString());
            pseudo = opseudo.toString();

            try {
                client = new Socket(ip, port);
                printEcrire = new PrintWriter(new BufferedWriter(new
                        OutputStreamWriter(
                                client.getOutputStream())), true);

                printEcrire.println("//message//"); //démarre une fenêtre chez le contact

                lecteur = new BufferedReader(new InputStreamReader(client.
                        getInputStream()));

                start();
            } catch (UnknownHostException ex) {
            } catch (IOException ex) {
            }
        }

        /**
         * @param s Socket
         */
        private BuildClient(Socket s) {
            this.client = s;

            //instanciation des attributs ip, port, pseudo à partir des infos
            //existantes dans les listes
            ip = client.getInetAddress().getHostAddress();
            port = Integer.parseInt(listeContactsPort.get(
                    listeContactsIP.indexOf(ip)).toString());
            pseudo = listeContactsPseudo.get(listeContactsIP.
                                             indexOf(ip)).toString();

            start();
        }

        public void run() {
            instancierComposantsClient();
            configurerComposantsClients();

            try {
                printEcrire = new PrintWriter(new BufferedWriter(new
                        OutputStreamWriter(
                                client.getOutputStream())), true);

                lecteur = new BufferedReader(new InputStreamReader(client.
                        getInputStream()));

            } catch (IOException ex) {
            }

            while (true) {
                try {
                    lecture = lecteur.readLine();
                    if (lecture.equals("//fermeture de la fenetre//")) {
                        try {
                            //fermeture
                            frameConv.setVisible(false);

                            printEcrire.flush();
                            printEcrire.close();
                            lecteur.close();
                            client.close();

                        } catch (Exception e) {
                        } finally {
                            break;
                        }
                    } else if (lecture.equals("//essaye d'envoyer un fichier//")) {
                        //tentative d'envoie de fichier
                        filename = lecteur.readLine();

                        tailleFichier = lecteur.readLine();
                        choix = JOptionPane.showConfirmDialog(null,
                                pseudo + " essaye de vous envoyer le fichier " +
                                filename + " de taille " + tailleFichier +
                                "\nAcceptez-vous?",
                                "Envoie de fichier", JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE, null);

                        switch (choix) {

                        case JOptionPane.YES_OPTION:
                            printEcrire.println(
                                    "//accepte l'envoie de fichier//");

                            boolAccept = false;
                            jfc.setDialogTitle("Enregistrer le fichier");
                            jfc.setApproveButtonText("Enregistrer");

                            choix = jfc.showOpenDialog(null);
                            if (choix == JFileChooser.APPROVE_OPTION) {
                                fichierRecu = jfc.getSelectedFile();
                            }
                            break;

                        case JOptionPane.NO_OPTION:
                            printEcrire.println(
                                    "//refuse l'envoie du fichier//");
                            break;
                        }

                    } else if (lecture.equals("//accepte l'envoie de fichier//")) {
                        boolAccept = false;
                        printEcrire.println("//debut du transfert//");
                        contenu +=
                                "<b>Accepte le transfert de fichiers</b><br>";
                        editor.setText(contenu);
                        editor.selectAll();
                        boutonAnnule.setEnabled(true);

                        /**
                         * EnvoyeurFichier.java
                         * permet d'envoyer un fichier, affiche le pourcentage
                         * du transfert et le taux en octets.
                         * Date: 01/01/2006
                         * @author Hassen Ben Tanfous
                         */
                        class EnvoyeurFichier extends Thread {
                            private int
                                    value, //valeur de progress
                                    count; //valeur de flux

                            private double pourcent;
                            private byte[] data; //données transmises
                            private long ltaille; //taille fichier

                            public EnvoyeurFichier() {
                                data = new byte[BUFFER];
                            }

                            public void run() {
                                ltaille = fichierEnvoye.length();

                                //configuration progressBar
                                progress.setMinimum(0);
                                progress.setMaximum((int) ltaille);
                                progress.setValue(value);

                                try {
                                    clientFichierEnvoye = new Socket(ip, port);
                                    BufferedOutputStream buffos = new
                                            BufferedOutputStream(
                                            clientFichierEnvoye.
                                            getOutputStream());

                                    BufferedInputStream buffis = new
                                            BufferedInputStream(new
                                            FileInputStream(fichierEnvoye.
                                            getAbsolutePath()));

                                    while ((count = buffis.read(data, 0, BUFFER)) !=
                                            -1) {

                                        buffos.write(data, 0, count);
                                        value += count;
                                        progress.setValue(value);

                                        //affichage valeur en octets
                                        lblTransfert1.setText(value + "/" +
                                                ltaille);

                                        //affichage valeur en pourcentage
                                        pourcent = (double) value / ltaille *
                                                100;
                                        lblTransfert2.setText(formatPourcent.
                                                format(pourcent) + "%");
                                    }

                                    msg.msgi("Transfert complet");
                                    boutonAnnule.setEnabled(false);
                                    progress.setValue(0);
                                    lblTransfert1.setText("");
                                    lblTransfert2.setText("");
                                    buffos.flush();
                                    buffos.close();
                                    buffis.close();

                                } catch (UnknownHostException ex) {
                                } catch (IOException ex) {
                                }
                            }
                        }


                        new EnvoyeurFichier().start();

                    } else if (lecture.equals("//debut du transfert//")) {
                        boutonAnnule.setEnabled(true);

                        /**
                         * ReceveurFichier.java
                         * traite la socket reçu et enregistre le flux
                         * sur le disque dur. Même configuration au niveau
                         * du transfert de fichier que EnvoyeurFichier
                         * @see EnvoyeurFichier.java
                         * Date: 01/01/2006
                         * @author Hassen Ben Tanfous
                         */
                        class ReceveurFichier extends Thread {
                            private int value, //valeur progress
                                    count; //valeur flux
                            private double pourcent;
                            private long ltaille; //taille fichier
                            private byte data[]; //données transmises

                            public ReceveurFichier() {
                                data = new byte[BUFFER];
                                clientFichierRecu = nouveau;
                            }

                            public void run() {
                                long ltaille = Long.parseLong(tailleFichier);

                                //config progress bar
                                progress.setMinimum(0);
                                progress.setMaximum((int) ltaille);
                                progress.setValue(value);

                                try {
                                    //socket du client reçu
                                    //@see GererClients
                                    BufferedInputStream buffis = new
                                            BufferedInputStream(
                                            clientFichierRecu.
                                            getInputStream());

                                    BufferedOutputStream buffos = new
                                            BufferedOutputStream(new
                                            FileOutputStream(fichierRecu.
                                            getAbsolutePath()));

                                    while ((count = buffis.read(data, 0, BUFFER)) !=
                                            -1) {
                                        buffos.write(data, 0, count);
                                        value += count;
                                        progress.setValue(value);

                                        //affichage en octets
                                        lblTransfert1.setText(value + "/" +
                                                ltaille);

                                        //affichage en pourcent
                                        pourcent = (double) value / ltaille *
                                                100;
                                        lblTransfert2.setText(formatPourcent.
                                                format(pourcent) + "%");
                                    }

                                    msg.msgi("Transfert complete");
                                    boutonAnnule.setEnabled(false);
                                    progress.setValue(0);
                                    lblTransfert1.setText("");
                                    lblTransfert2.setText("");
                                    buffos.flush();
                                    buffos.close();
                                    buffis.close();

                                } catch (IOException ex) {
                                }
                            }
                        }


                        new ReceveurFichier().start();

                    } else if (lecture.equals(
                            "//veut demarrer conversation VOIP//")) {
                        clientVOIP = new VOIPClient(ip, port);
                        //ajout de code pour une demande avant de démarrer la conversation VOIP
//                        int choix = JOptionPane.showOptionDialog(null, "msg",
//                                "titre", JOptionPane.YES_NO_OPTION,
//                                JOptionPane.QUESTION_MESSAGE, null, null, null);
//                        if (choix == JOptionPane.YES_OPTION) {
//                            boutonDemarrer.setEnabled(false);
//                            boutonArreter.setEnabled(true);
//                        }

                        //permutation des boutons
                        boutonDemarrer.setEnabled(false);
                        boutonArreter.setEnabled(true);

                    } else if (lecture.equals(
                            "//veut arreter la conversation VOIP//")) {
                        //permutation des boutons
                        boutonDemarrer.setEnabled(true);
                        boutonArreter.setEnabled(false);

                        //arrete la conversation
                        boolStopTalking = true;

                    } else if (lecture.equals("//annule//")) { //annulation du transfert de fichier
                        contenu +=
                                "<b>Annulation du transfert de fichier</b><br>";
                        editor.setText(contenu);
                        editor.selectAll();
                        //permutation bouton
                        boutonAnnule.setEnabled(false);
                        progress.setValue(0);

                    } else if (lecture.equals("//refuse l'envoie du fichier//")) {
                        boutonAnnule.setEnabled(false);
                        contenu += "<b>Refuse le transfert du fichier</b><br>";
                        editor.setText(contenu);
                        editor.selectAll();
                    } else {
                        //ajout du texte chat
                        contenu += lecture;
                        editor.setText(contenu);
                        editor.selectAll();
                    }
                } catch (IOException ex) {
                }
            }
        }

        private void configurerComposantsClients() {

            //Modèle
            //configuration des boutons et du focus
            boutonSend.setToolTipText("permet d'envoyer des fichiers");
            editor.setFocusable(false);
            boutonSend.setFocusable(false);
            boutonAnnule.setFocusable(false);
            boutonAnnule.setEnabled(false);
            boutonDemarrer.setFocusable(false);
            boutonArreter.setFocusable(false);
            boutonArreter.setEnabled(false);

            //tentative de focus sur le texte
            txt.setFocusable(true);
            txt.requestFocus();
            txt.grabFocus(); //== requestFocus()

            editor.setContentType("text/html");
            editor.setBackground(Color.BLACK);
            editor.setEditable(false);

            jfc.setDialogTitle("Envoie de fichier à " + pseudo);
            jfc.setMultiSelectionEnabled(false);
            jfc.setApproveButtonText("Send");
            jfc.setApproveButtonToolTipText("Envoie le fichier à " + pseudo);

            //ajout de la fermeture de la fenêtre par la touche ESC
            frameConv.addWindowListener(new WindowListener() {
                public void windowOpened(WindowEvent e) {

                }

                public void windowClosing(WindowEvent e) {
                    frameConv.setVisible(false);
                    printEcrire.println("//fermeture de la fenetre//");
                    try {
                        printEcrire.close();
                        lecteur.close();
                        client.close();
                    } catch (Exception ex) {
                    }

                }

                public void windowClosed(WindowEvent e) {
                }

                public void windowIconified(WindowEvent e) {

                }

                public void windowDeiconified(WindowEvent e) {

                }

                public void windowActivated(WindowEvent e) {

                }

                public void windowDeactivated(WindowEvent e) {

                }
            });

            //ajout des listeners pour les boutons et le texte
            txt.addActionListener(alChat);
            boutonSend.addActionListener(alChat);
            boutonAnnule.addActionListener(alChat);

            boutonDemarrer.addActionListener(alVOIP);
            boutonArreter.addActionListener(alVOIP);

            AbstractAction fermer = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    frameConv.setVisible(false);
                    printEcrire.println("//fermeture de la fenetre//");
                }
            };

            frameConv.getRootPane().getActionMap().put("fermer", fermer);
            int stdMask = Toolkit.getDefaultToolkit().
                          getMenuShortcutKeyMask();
            InputMap im =
                    frameConv.getRootPane().getInputMap(JComponent.
                    WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fermer");

            cConv.setLayout(null);
            ajouterComposant(cConv, scrollEditor, 20, 20, 300, 250);
            ajouterComposant(cConv, boutonSend, 350, 20, 100, 20);

            ajouterComposant(cConv, lblTransfert1, 330, 80, 150, 20);
            ajouterComposant(cConv, lblTransfert2, 350, 120, 100, 20);

            ajouterComposant(cConv, progress, 20, 280, 300, 15);
            ajouterComposant(cConv, txt, 20, 300, 300, 20);

            ajouterComposant(cConv, boutonAnnule, 350, 150, 100, 20);
            ajouterComposant(cConv, boutonDemarrer, 350, 200, 100, 20);
            ajouterComposant(cConv, boutonArreter, 350, 250, 100, 20);

            frameConv.setLocation(200, 200);
            frameConv.setResizable(false);
            frameConv.setSize(500, 400);

            frameConv.setVisible(true);
        }

        private void instancierComposantsClient() {
            frameConv = new JFrame(pseudo + " conversation");
            cConv = frameConv.getContentPane();

            boutonSend = new JButton("Send file");
            boutonAnnule = new JButton("Annuler");

            boutonDemarrer = new JButton("Start VOIP");
            boutonArreter = new JButton("Stop VOIP");

            txt = new JTextField();
            editor = new JEditorPane();

            lblTransfert1 = new JLabel();
            lblTransfert2 = new JLabel();

            scrollEditor = new JScrollPane(editor);

            progress = new JProgressBar();
            jfc = new JFileChooser();
        }

        /**
         * gère le texte d'entrée, les boutons d'envoie et d'annulation
         * de fichiers
         */
        private ActionListener alChat = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == txt) { //entrée texte
                    //contenu HTML
                    contenu += "<body text=\"00FF33\"><b>" +
                            txtPseudo.getText() +
                            "</b>: " +
                            txt.getText() + "<br>";
                    editor.setText(contenu);
                    editor.selectAll();
                    printEcrire.println("<body text=\"FF0000\"><b>" +
                                        txtPseudo.getText() +
                                        "</b>: " + txt.getText() + "<br>");

                    txt.setText("");

                } else if (e.getSource() == boutonSend) { //envoie de fichier
                    jfc.setDialogTitle("Envoie de fichier à " + pseudo);
                    jfc.setApproveButtonText("Envoyer");
                    int choix = jfc.showOpenDialog(null);

                    if (choix == JFileChooser.APPROVE_OPTION) {
                        boutonAnnule.setEnabled(true);
                        fichierEnvoye = jfc.getSelectedFile();
                        printEcrire.println(
                                "//essaye d'envoyer un fichier//");
                        printEcrire.println(fichierEnvoye.getName());
                        printEcrire.println(fichierEnvoye.length());
                    }

                } else if (e.getSource() == boutonAnnule) { //annulation du transfert
                    try {
                        clientFichierRecu.close();
                        printEcrire.println("//annule//");
                    } catch (Exception ex1) {
                    }

                    try {
                        clientFichierEnvoye.close();
                        printEcrire.println("//annule//");
                    } catch (Exception ex) {
                    } finally {
                        boutonAnnule.setEnabled(false);
                        progress.setValue(0);
                    }
                }
            }
        };

        /**
         * permet de gérer les conversations VOIP et les boutons
         */
        private ActionListener alVOIP = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == boutonDemarrer) { //démarre VOIP
                    //permutation
                    boutonArreter.setEnabled(true);
                    boutonDemarrer.setEnabled(false);

                    try {
                        //tentative de démarrer
                        printEcrire.println(
                                "//veut demarrer conversation VOIP//");

                        //permet de recevoir la Socket
                        boolVOIP = true;

                    } catch (Exception ex) {
                    }
                } else if (e.getSource() == boutonArreter) {
                    printEcrire.println("//veut arreter la conversation VOIP//");

                    //arrête le flux
                    boolStopTalking = true;

                    //permutation
                    boutonDemarrer.setEnabled(true);
                    boutonArreter.setEnabled(false);
                }
            }
        };
    }


    /**
     * VOIPClient.java
     * permet de gérer les conversations VOIP
     * fonctionne parfaitement sur un réseau privé, aucune interruption, aucune discontinuité,
     * aucune perte de signal visible, volume moyen, pas de répétitions
     *
     * Si le son est incorrect.
     * @see #confAudio();
     * pour la configuration exacte de votre matériel audio
     *
     * @version 1.0
     * Date: 02/01/2006
     * @author Hassen Ben Tanfous
     */
    private class VOIPClient extends Thread {
        private String ip,
                pseudo;
        private int port;
        private Socket client;

        //25000 en LAN
        //x d'après votre vitesse d'Upload et Download sur Internet
        final int VOIP_BUFFER = 25000;

        /**
         * @param ip String
         * @param port int
         */
        public VOIPClient(String ip, int port) {
            this.ip = ip;
            this.port = port;
            try {
                client = new Socket(ip, port);

                this.start();
            } catch (Exception e) {
            }
        }

        public VOIPClient(Socket s) {
            client = s;
//            ip = client.getInetAddress().getHostAddress();
//            port = Integer.parseInt(listeContactsPort.get(
//                    listeContactsIP.indexOf(ip)).toString());
//            pseudo = listeContactsPseudo.get(
//                    listeContactsIP.
//                    indexOf(ip)).toString();

            this.start();
        }

        public void run() {

            /**
             * VOIPEcouteur.java
             * permet de lire le flux de la Socket et de l'écrire afin d'être
             * entendu dans les écouteurs
             * @version 1.0
             * Date: 02/01/2006
             * @author Hassen Ben Tanfous
             */
            class VOIPEcouteur extends Thread {

                private BufferedInputStream buffis;
                private InputStream is;

                private byte[] data;
                private int count;

                private AudioFormat audio;
                private TargetDataLine ligneCible;
                private SourceDataLine ligneSource;

                private AudioInputStream audiois;

                public VOIPEcouteur() {
                    try {
                        buffis = new BufferedInputStream(client.getInputStream());
                        //configuration Audio
                        audio = confAudio();
                    } catch (IOException ex) {
                    }

                }

                public void run() {
                    data = new byte[VOIP_BUFFER];

                    try {
                        while ((count = buffis.read(data, 0, data.length)) !=
                                        -1 && !boolStopTalking) {

                            is = new ByteArrayInputStream(data);

                            audiois = new AudioInputStream(is, audio,
                                    data.length / audio.getFrameSize());

                            DataLine.Info dataLineInfo = new DataLine.Info(
                                    SourceDataLine.class, audio);

                            ligneSource = (SourceDataLine) AudioSystem.getLine(
                                    dataLineInfo);

                            ligneSource.open(audio);
                            ligneSource.start();

                            while ((count = audiois.read(data, 0, data.length)) !=
                                            -1) {
                                if (count > 0) {
                                    ligneSource.write(data, 0, count);
                                }
                            }
                        }

                        ligneSource.drain();
                        ligneSource.close();

                        client.close();
                    } catch (IOException ex) {
                    } catch (LineUnavailableException ex) {
                    }
                }
            }


            /**
             * VOIPParleur.java
             * permet de transférer le flux du Microphone vers la socket
             * @version 1.0
             * Date: 02/01/2006
             * @author Hassen Ben Tanfous
             */
            class VOIPParleur extends Thread {
                private BufferedOutputStream buffos;
                private AudioInputStream audiois;

                private TargetDataLine ligneCible;
                private ByteArrayOutputStream byteos;

                private AudioFormat audio;

                private byte[] data;
                private int count;

                public VOIPParleur() {
                    try {
                        buffos = new BufferedOutputStream(client.
                                getOutputStream());

                        audio = confAudio();

                    } catch (IOException ex) {
                    }

                }

                public void run() {
                    DataLine.Info dataLineInfo = new DataLine.Info(
                            TargetDataLine.class, audio);

                    try {
                        ligneCible = (TargetDataLine) AudioSystem.getLine(
                                dataLineInfo);
                    } catch (LineUnavailableException ex) {
                    }

                    try {
                        ligneCible.open(audio);
                    } catch (LineUnavailableException ex1) {
                    }

                    ligneCible.start();
                    data = new byte[VOIP_BUFFER];
                    boolStopTalking = false;

                    try {

                        while (!boolStopTalking) {
                            count = ligneCible.read(data, 0, data.length);
                            if (count > 0) {
                                buffos.write(data, 0, count);
                            }
                        }

                        buffos.close();

                        client.close();
                    } catch (Exception e) {
                    }
                }
            }


            new VOIPParleur().start();
            new VOIPEcouteur().start();
        }

        /**
         * configuration audio
         * Si le son est incorrect, changer les paramètres de cette méthode
         * @return AudioFormat
         */
        private AudioFormat confAudio() {
            //configuration copiée

            //8000,11025,16000,22050,44100
            float sampleRate = 8000.0F;
            //8,16
            int sampleSizeInBits = 16;
            //1,2
            int channels = 2;
            boolean signed = true;
            boolean bigEndian = false;

            return new AudioFormat(sampleRate, sampleSizeInBits,
                                   channels, signed,
                                   bigEndian);
        }
    }


    /**
     * ReponseOffline.java
     * permet d'envoyer une socket à tous les contacts online pour les aviser
     * que vous fermez votre serveur
     * Date: 31/12/2005
     * @author Hassen Ben Tanfous
     */
    private class ReponseOffline extends Thread {

        private String ip;
        private int port;
        private Socket client;
        private PrintWriter ecrire;

        /**
         * @param oip Object
         * @param oport Object
         */
        private ReponseOffline(Object oip, Object oport) {
            ip = oip.toString();
            port = Integer.parseInt(oport.toString());

            start();
        }

        public void run() {
            try {
                client = new Socket(ip, port);
                if (client.isConnected()) {
                    ecrire = new PrintWriter(new BufferedWriter(new
                            OutputStreamWriter(client.getOutputStream())), true);

                    ecrire.println("//je suis offline//");
                    ecrire.println(txtConnectIP.getText());

                    ecrire.flush();
                    ecrire.close();
                    client.close();
                }
            } catch (IOException ex) {
            }
        }
    }


    /**
     * RepondreClient.java
     * RépondreClient permet de gérer toutes les sockets pour les traiter
     * séparément
     * changement de pseudo, contact online/offline etc.
     * Date: 31/12/2005
     * @author Hassen Ben Tanfous
     */
    private class RepondreClient extends Thread {

        private String
                ip,
                pseudo;
        private int port;
        private Socket client;

        private PrintWriter reponse;
        String lecture;

        /**
         * @param s Socket
         * @param str String
         */
        public RepondreClient(Socket s, String str) {
            client = s;
            lecture = str;

            start();
        }

        public void run() {
            try {
                while (true && client != null) {

                    if (lecture.equals("//je suis online//")) {
                        //lecture
                        ip = lireLecture.readLine();
                        port = Integer.parseInt(lireLecture.readLine());
                        pseudo = lireLecture.readLine();

                        if (listeContactsIP.indexOf(ip) == -1) {
                            listeContactsIP.addElement(ip);
                            listeContactsPort.addElement(Integer.toString(port));
                            listeContactsPseudo.addElement(pseudo);
                            listeOnlineIP.addElement(ip);
                            listeOnlinePseudo.addElement(pseudo);

                        } else if (listeContactsIP.indexOf(ip) != -1) {
                            if (listeOfflineIP.indexOf(ip) != -1) {
                                listeOfflinePseudo.removeElementAt(
                                        listeOfflineIP.indexOf(ip));
                                listeOfflineIP.removeElementAt(listeOfflineIP.
                                        indexOf(ip));

                                listeOnlineIP.addElement(ip);
                                listeOnlinePseudo.addElement(pseudo);
                            }
                        }

                        Socket socketPseudo = new Socket(ip, port);
                        PrintWriter ecrire = new PrintWriter(new BufferedWriter(new
                                OutputStreamWriter(socketPseudo.getOutputStream())), true);

                        ecrire.println("//mon nouveau pseudo est//");
                        ecrire.println(ip);
                        ecrire.println(txtPseudo.getText());
                        socketPseudo.close();
                        ecrire.close();
                        lireLecture.close();
                        client.close();
                        break;

                    } else if (lecture.equals("//je suis offline//")) {
                        String offlineIP = lireLecture.readLine();

                        listeOfflinePseudo.addElement(listeOnlinePseudo.get(
                                listeOnlineIP.indexOf(offlineIP)));
                        listeOfflineIP.addElement(offlineIP);

                        listeOnlinePseudo.removeElementAt(listeOnlineIP.indexOf(
                                offlineIP));
                        listeOnlineIP.removeElementAt(listeOnlineIP.indexOf(
                                offlineIP));

                        lireLecture.close();
                        client.close();
                        break;

                    } else if (lecture.equals("//mon nouveau pseudo est//")) {
                        ip = lireLecture.readLine();
                        pseudo = lireLecture.readLine();

                        listeOnlinePseudo.setElementAt(pseudo,
                                listeOnlineIP.indexOf(ip));

                        listeContactsPseudo.setElementAt(pseudo,
                                listeOnlineIP.indexOf(ip));

                        lireLecture.close();
                        client.close();
                        break;

                    } else {
                        lireLecture.close();
                        client.close();
                        break;
                    }
                }
            } catch (IOException ex) {
            }
        }
    }


    /**
     * GererClient.java
     * permet d'attendre qu'un client se connecte au serveur
     * pour exécuter les services offerts au client
     * Date: 31/12/2005
     * @version 1.0
     * @author Hassen Ben Tanfous
     *
     */
    private class GererClient extends Thread {
        private Socket client;
        private String lecture;

        private GererClient() {
            start();
        }

        public void run() {
            try {
                while (!server.isClosed()) {
                    client = server.accept();
                    if (!boolAccept) { //pour l'envoie de fichier
                        nouveau = client;
                        boolAccept = true;
                        return;
                    }

                    if (boolVOIP) { //vérification pour démarrer une conversation VOIP
                        new VOIPClient(client);
                        boolVOIP = false;

                    } else { //autre traitement spécifique qui nécessite l'ouverture du flux

                        lireLecture = new BufferedReader(new InputStreamReader(
                                client.getInputStream()));

                        lecture = lireLecture.readLine();

                        if (lecture.equals("//message//")) { //veut démarrer une fenêtre de conversation
                            new BuildClient(client);

                        } else { //autre traitement (pseudo etc)
                            new RepondreClient(client, lecture);
                        }
                    }
                }
            } catch (IOException ex1) {
                try {
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }


    /**
     * QuestionConnectedContacts.java
     * permet d'utiliser la liste de contacts par défaut pour déterminer
     * qui sont les contacts online ou offline
     * et aussi de combiner le service d'ajout de contacts avec celui de la
     * connexion
     * Date:  31/12/2005
     * @author Hassen Ben Tanfous
     */
    private class QuestionConnectedContacts extends Thread {
        private Socket contact;
        private String
                ip,
                pseudo;
        private int port;
        private PrintWriter ecrire;
        private QuestionConnectedContacts(Object oip,
                                          Object oport,
                                          Object opseudo) {

            this.ip = oip.toString();
            this.pseudo = opseudo.toString();
            try {
                this.port = Integer.parseInt(oport.toString());

                start();
            } catch (NumberFormatException ex) {
                msg.msge("Erreur durant l'ajout d'un de vos contacts\n" +
                         "Port invalide");
            }
        }

        public void run() {
            try {
                contact = new Socket(ip, port);

                if (contact.isConnected()) {
                    ecrire = new PrintWriter(new BufferedWriter(new
                            OutputStreamWriter(contact.
                                               getOutputStream())), true);

                    listeOnlineIP.addElement(ip);
                    listeOnlinePseudo.addElement(pseudo);
                    if (listeContactsIP.indexOf(ip) == -1) {
                        listeContactsIP.addElement(ip);
                        listeContactsPseudo.addElement(pseudo);
                        listeContactsPort.addElement(Integer.toString(port));
                    }
                    ecrire.println("//je suis online//");
                    ecrire.println(txtConnectIP.getText());
                    ecrire.println(txtConnectPort.getText());
                    ecrire.println(txtPseudo.getText());
                    ecrire.flush();
                    ecrire.close();
                    contact.close();
                }
            } catch (Exception e) {
                listeOfflineIP.addElement(ip);
                listeOfflinePseudo.addElement(pseudo);
                if (listeContactsIP.indexOf(ip) == -1) {
                    listeContactsIP.addElement(ip);
                    listeContactsPseudo.addElement(pseudo);
                    listeContactsPort.addElement(Integer.toString(port));
                }
            }
        }
    }
}
