
const ip = 'localhost'
const port = '8080'
export const host = `http://${ip}:${port}`

export const updateObject = (state, updatedObject) => {
    return {
        ...state,
        ...updatedObject
    }
}

export const toTitleCase = (str) => {
    if (!!str)
    return str.replace(
      /\w\S*/g,
      text => text.charAt(0).toUpperCase() + text.substring(1).toLowerCase()
    );
    return "____"
  }


  export  const itemImage = host+"/admin/item/image/";
  export const storeImage = host+"/admin/store/image/"
  export const userImage = host+"/admin/auth/profile/"


  export const suId = 0;
  export const projectName= "Swami Sales";
  export const rowsPerPageOptions=[10, 25 , 50]