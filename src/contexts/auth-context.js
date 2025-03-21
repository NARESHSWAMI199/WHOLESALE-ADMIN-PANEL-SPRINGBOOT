import { createContext, useContext, useEffect, useReducer, useRef } from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import { host } from 'src/utils/util';

const HANDLERS = {
  INITIALIZE: 'INITIALIZE',
  SIGN_IN: 'SIGN_IN',
  SIGN_OUT: 'SIGN_OUT',
  UPDATE_USER : 'UPDATE_USER',
  UPDATE_PAGINATIONS : 'UPDATE_PAGINATIONS'
};

let authToken = () => {
  try {
    return window.sessionStorage.getItem('token');
  } catch (err) {
    console.error(err);
  }
}

let getUser = () => {
  try {
    let user = window.sessionStorage.getItem('user');
    user = !!user ? JSON.parse(user) : null;
    return user
  } catch (err) {
    console.error(err);
  }
}


let getPaginations = () => {
  if (typeof window === 'undefined') return null;
  try {
    let paginations = window.sessionStorage.getItem('paginations');
    paginations = !!paginations ? JSON.parse(paginations) : null;
    return paginations
  } catch (err) {
    console.error(err);
  }
}


const initialState = {
  token: authToken(),
  isAuthenticated: false,
  isLoading: true,
  user: getUser(),
  paginations : getPaginations()
};

const handlers = {
  [HANDLERS.INITIALIZE]: (state, action) => {
    const user = action.payload;

    return {
      ...state,
      ...(
        // if payload (user) is provided, then is authenticated
        user
          ? ({
            isAuthenticated: true,
            isLoading: false,
            user
          })
          : ({
            isLoading: false
          })
      )
    };
  },
  [HANDLERS.SIGN_IN]: (state, action) => {
    const user = action.payload;
    const paginations = action.paginations;
    return {
      ...state,
      token: action.token,
      isAuthenticated: true,
      user,paginations
    };
  },
  [HANDLERS.SIGN_OUT]: (state) => {
    return {
      ...state,
      isAuthenticated: false,
      user: null,
      paginations : null
    };
  },
  [HANDLERS.UPDATE_USER]: (state, action) => {
    const user = action.payload;
    return {
      ...state,
      isAuthenticated: false,
      user
    };
  },
  // updating user paginations
  [HANDLERS.UPDATE_PAGINATIONS]: (state,action) => {
    const paginations = action.paginations
    return {
      ...state,
      paginations
    };
  }

};

const reducer = (state, action) => (
  handlers[action.type] ? handlers[action.type](state, action) : state
);

// The role of this context is to propagate authentication state through the App tree.

export const AuthContext = createContext({ undefined });

export const AuthProvider = (props) => {
  const { children } = props;

  /** when you call dispatch then the dispatched data passed in reducer as action */
  const [state, dispatch] = useReducer(reducer, initialState);
  const initialized = useRef(false);

  const initialize = async () => {
    // Prevent from calling twice in development mode with React.StrictMode enabled
    if (initialized.current) {
      return;
    }

    initialized.current = true;

    let isAuthenticated = false;

    try {
      isAuthenticated = window.sessionStorage.getItem('authenticated') === 'true';
    } catch (err) {
      console.error(err);
    }

    if (isAuthenticated) {
      const user = getUser();
      dispatch({
        type: HANDLERS.INITIALIZE,
        payload: user,
      });
    } else {
      dispatch({
        type: HANDLERS.INITIALIZE
      });
    }
  };

  useEffect(
    () => {
      initialize();
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    []
  );



  const signIn = async (email, password, method) => {

    let baseUrl = host + "/admin/auth/login"
    if (method == 'OTP') {
      baseUrl = host + "/admin/auth/login/otp"
    }

    await axios.post(baseUrl, {
      email: email,
      password: password
    })
      .then(res => {
        const token = res.data.token
        const paginations = res.data.paginations
        const user = res.data.user
        if (typeof window !== 'undefined') {
          window.sessionStorage.setItem('authenticated', 'true');
          window.sessionStorage.setItem('token', token);
          window.sessionStorage.setItem("user", JSON.stringify(user))
          window.sessionStorage.setItem("paginations",JSON.stringify(paginations))
        }
        dispatch({
          type: HANDLERS.SIGN_IN,
          token: token,
          payload: user,
          paginations : paginations
        });

        setTimeout(() => {
          signOut()
        }, 5 * 60 * 60* 1000)
      })
      .catch(err => {
        const errorMessage = (!!err.response) ? err.response.data.message : err.message;
        console.log(errorMessage)
        throw new Error(errorMessage)
      })
  };

  const updateUserDetail = async (slug) => {
    axios.defaults.headers = {
      Authorization: initialState.token
    }
    await axios.get(host + "/admin/auth/detail/" + slug)
      .then(res => {
        const user = res.data.res
        window.sessionStorage.setItem("user", JSON.stringify(user))

        dispatch({
          type: HANDLERS.UPDATE_USER,
          payload: user
        });
      })
      .catch(err => {
        const errorMessage = (!!err.response) ? err.response.data.message : err.message;
        console.log(errorMessage)
        //throw new Error(errorMessage)
      })
  };



  const signUp = async (email, name, password) => {
    throw new Error('Sign up is not implemented');
  };

  const signOut = () => {
    if (typeof window !== 'undefined') {
      window.sessionStorage.removeItem('authenticated');
      window.sessionStorage.removeItem('user');
      window.sessionStorage.removeItem('token');
      window.sessionStorage.removeItem("paginations");
    }
    dispatch({
      type: HANDLERS.SIGN_OUT
    });
  };


  const updateUser = (updatedUser) => {
    window.sessionStorage.setItem('user', updateUser);
    dispatch({
      type: HANDLERS.SIGN_OUT
    });
  };


  const updatePaginations = (rowsNumber,pagination) =>{
    let preSavePaginations =  getPaginations();
    for(let label of  Object.values(preSavePaginations)){
      if(pagination?.fieldFor === label.pagination?.fieldFor){
        label.rowsNumber = rowsNumber
      }
    }
    console.log(preSavePaginations)
    if (typeof window !== 'undefined') {
      window.sessionStorage.setItem("paginations",JSON.stringify(preSavePaginations))
    }
  
    dispatch({
      type : HANDLERS.UPDATE_PAGINATIONS,
      paginations : preSavePaginations
    })
  
  }

  return (
    <AuthContext.Provider
      value={{
        ...state,
        signIn,
        signUp,
        signOut,
        updateUserDetail,
        updatePaginations
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

AuthProvider.propTypes = {
  children: PropTypes.node
};

export const AuthConsumer = AuthContext.Consumer;

export const useAuthContext = () => useContext(AuthContext);
