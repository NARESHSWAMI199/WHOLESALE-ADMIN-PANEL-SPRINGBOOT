import React, { useEffect, useState } from 'react';
import { Spin } from 'antd';
import { set } from 'nprogress';

const Spinner = (props) => {

    const [show ,setShow] = useState("none")
    useEffect(()=>{
        setShow(props.show)
    },[props.show])
    return <Spin style={{display : show}} />;
}

export default Spinner;