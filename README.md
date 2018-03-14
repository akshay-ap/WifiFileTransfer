Peer to peer form tansfer for `ODK collect` using Wifi.

Note :

    Lead device = Server

    Other device = Client

Advantages : 
1. No HARD CODING OF PORT NUMBER (use next available port).
2. Easy to use

How to use?

You need two devices with android API 16 or above.
On 1st device:
1. Click `Lead device`.
2. Wait for QRCode to be seen.

On 2nd device:
4. Click `Join Lead`.
5. Scan QRCode.
6. If `Connect` button if seen else click `Join lead` again.
7. Wait for connection to be established.

Transfer forms.
1. Click `RECEIVE FORM` on one device.
2. `SEND FORMS` on other device. Select forms to be sent. Click send button.

Screen shots:
Lead device Other Device

<img src="/screenshots/asus_gif.gif" width=30%> <img src="/screenshots/moto_gif.gif" width=30%>

Feature:
Connect two devices using QRCODE.
Semi-bidirectional form transfer is possible.

The `lead` device:

    1.creats a hotspot

    2. Accetps Socket connection

    3. Creates QRCode

The `other` device:

    1. Scan QRCode

    3. Connects to hotspot

    4. Connect as Client

Issues:
Connecting to hotspot sometimes fails.
Yet to handle broken connection during form transfer.
