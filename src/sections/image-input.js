

import { Upload } from 'antd'
import React from 'react'

const ImageInput  = (props) => {

const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState('');
  const [fileList, setFileList] = useState([
    {
      uid: '-1',
      name: 'image.png',
      status: 'done',
      url: 'https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png',
    }])

    const getBase64 = (file) =>
        new Promise((resolve, reject) => {
          const reader = new FileReader();
          reader.readAsDataURL(file);
          reader.onload = () => resolve(reader.result);
          reader.onerror = (error) => reject(error);
        });
      

        const handlePreview = async (file) => {
            if (!file.url && !file.preview) {
              file.preview = await getBase64(file.originFileObj);
            }
            setPreviewImage(file.url || file.preview);
            setPreviewOpen(true);
        };

    const handleChange = () => {
        props.onSubmit();
    }

  return (<>
             {/* Image upload  */}
             <Upload
             action="https://660d2bd96ddfa2943b33731c.mockapi.io/api/upload"
             listType="picture-circle"
             fileList={fileList}
             onPreview={handlePreview}
             onChange={handleChange}
           >
             {fileList.length >= 8 ? null : uploadButton}
           </Upload>
           {previewImage && (
             <Image
               wrapperStyle={{ display: 'none' }}
               preview={{
                 visible: previewOpen,
                 onVisibleChange: (visible) => setPreviewOpen(visible),
                 afterOpenChange: (visible) => !visible && setPreviewImage(''),
               }}
               src={previewImage}
             />
           )}
           </>
  )
}

export default ImageInput;