from rembg import remove
import cv2
for i in range(1, 9382):
    input_path = 'input_test/short_sleeve/'+str(i)+'_short_sleeve.png'
    output_path = 'output_test/short_sleeve/'+str(i)+'_short_sleeve.png'
    input_re = cv2.imread(input_path)
    output = remove(input_re)
    cv2.imwrite(output_path, output)
