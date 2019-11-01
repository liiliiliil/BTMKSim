import sys
from bluetooth import *
import struct

class Bluetooth:
    
    def __init__(self):
        self.server_sock = BluetoothSocket(RFCOMM)
        self.server_sock.bind(("",PORT_ANY))
        self.server_sock.listen(1)

        self.port = self.server_sock.getsockname()[1]

        self.uuid = "59d45e75-9b1f-465b-a6b5-08e68103a228"

        self.client_sock = None
        self.client_info = None

    def connect(self):
        advertise_service(self.server_sock, "SampleServer",
                        service_id = self.uuid,
                        service_classes = [ self.uuid, SERIAL_PORT_CLASS ],
                        profiles = [ SERIAL_PORT_PROFILE ], 
        #                   protocols = [ OBEX_UUID ] 
                            )
                   
        print("Waiting for connection on RFCOMM channel %d" % self.port)

        self.client_sock, self.client_info = self.server_sock.accept()
        print("Accepted connection from channel ", self.client_info)

    def run(self):
        try:
            data = self.client_sock.recv(1024)
            if len(data) == 9:
                decoded_data = struct.unpack('>b2f', data)
                print('\rreceived:  '+str(decoded_data), end='                    ')
                return decoded_data
            elif len(data) == 2:
                decoded_data = struct.unpack('>bc', data)
                print('\rreceived:  '+str(decoded_data), end='                                        ')
                return decoded_data
            
            print('\rReceived data are uncorrect. Expect len(data) == 9 or len(data) == 2, but len(data) == %d' % len(data), end='                    ')
            return

        except IOError:
            pass


    def disconnect(self):
        self.client_sock.close()
        self.server_sock.close()
        print("Sock has been closed.")
