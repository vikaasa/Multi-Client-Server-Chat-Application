CNT 5106 Project
Ramit Suri, UFID: 48334388
Vikaasa Ramdas Thandu Venkat Kumar, UFID: 44005810

The submission has CNT5106_RAMIT_48334388_VIKAASA_44005810.zip

Steps to run the application
1. Create directories for different clients, copying the contents of the zip file into those.
2. Start a server in one of the directories by running the command 'java server'.
3. Start each of the clients by running the command 'java client <username>' in their respective directories.
4. Send message in the following format 
		<message_type> <"message/file_address"> <send_type> <username>

message_type can be  TEXT, FILE, ADMIN
message/file_address is the text message or address of the file that the client wants to send 
send_type is UNICAST, BLOCKCAST, BROADCAST
username denotes the username to send a UNICAST message to or the username that needs to be blocked in BLOCKCAST. BROADCAST message doesn't need a username.

Contributions
Ramit Suri: Server part
Vikaasa Ramdas Thandu Venkat Kumar: Client part