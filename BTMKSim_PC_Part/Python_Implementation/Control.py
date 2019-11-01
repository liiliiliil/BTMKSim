import pyautogui
pyautogui.FAILSAFE = False

class Control:

    def __init__(self, range_x=1.2, range_y=1.2):
        self.screen_width, self.screen_height = pyautogui.size()
        self.range_x = range_x
        self.range_y = range_y
        self.multiplier_x = self.screen_width / self.range_x
        self.multiplier_y = self.screen_height / self.range_y

        self.left_arrow = chr(27)
        self.right_arrow = chr(26)

        # self.pos_x = 0
        # self.pos_y = 0

    def process_orientation(self, orientation):
        # orientation[0] = min(range_x, orientation[0])
        # orientation[1] = min(range_y, orientation[1])

        return orientation[0] * self.multiplier_x, orientation[1] * self.multiplier_y
    
    def move_mouse(self, orientation):
        pos_x, pos_y = self.process_orientation(orientation)
        print('pos: %f, %f' % (pos_x, pos_y), end='       ')
        pyautogui.moveTo(pos_x, pos_y, 0)
        # pyautogui.moveRel(pos_x-self.pos_x, pos_y-self.pos_y)
        # self.pos_x, self.pos_y = pos_x, pos_y
    
    def left_click(self):
        print('\nleft_click')
        pyautogui.click(button='left')
    
    def right_click(self):
        print('\nright_click')
        pyautogui.click(button='right')
    
    def press(self, key):
        if (self.left_arrow == key):
            key = 'left'
        elif (self.right_arrow == key):
            key = 'right'
        print('\npress %s' % key)
        pyautogui.press(key)
        # pyautogui.keyDown(key)
        # pyautogui.keyUp(key)

