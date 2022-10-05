from rembg import remove
import cv2

for i in range(1,11):
    input_path = 'input_test/'+str(i)+'_short_pants.png'
    output_path = 'output_test/'+str(i)+'_short_pants.png'

    input_re = cv2.imread(input_path)
    output = remove(input_re)
    cv2.imwrite(output_path, output)
