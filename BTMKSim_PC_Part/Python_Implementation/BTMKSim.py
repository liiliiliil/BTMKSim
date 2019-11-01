from Bluetooth import Bluetooth
from Control import Control

range_X = 1.2
range_Y = 1.2

MASSAGE_MOVE = 0
MASSAGE_LEFT_CLICK = 1
MASSAGE_RIGHT_CLICK = 2

MASSAGE_PRESS = 5

class BMKSim:

    def __init__(self, range_x=1.2, range_y=1.2):
        self.bluetooth = Bluetooth()
        self.control = Control(range_x, range_y)

        self.max_try = 500

    def run(self):
        self.bluetooth.connect()
        error_count = 0
        while True:
            data = self.bluetooth.run()
            if data:
                error_count = 0
                if data[0] == MASSAGE_MOVE:
                    self.control.move_mouse(data[1:])
                elif data[0] == MASSAGE_LEFT_CLICK:
                    self.control.left_click()
                elif data[0] == MASSAGE_RIGHT_CLICK:
                    self.control.right_click()
                elif data[0] == MASSAGE_PRESS:  # test: 
                    self.control.press(bytes.decode(data[1]))
                
            else:
                error_count += 1
            
            if error_count == self.max_try:
                break

        print('\nDisconnected...')
        self.bluetooth.disconnect()


def main():

    simulator = BMKSim(range_X, range_Y)
    simulator.run()


if __name__ == "__main__":
    main()