import ChartBarIcon from '@heroicons/react/24/solid/ChartBarIcon';
import CogIcon from '@heroicons/react/24/solid/CogIcon';
import ShoppingBagIcon from '@heroicons/react/24/solid/ShoppingBagIcon';
import UserIcon from '@heroicons/react/24/solid/UserIcon';
import UsersIcon from '@heroicons/react/24/solid/UsersIcon';
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import BadgeIcon from '@mui/icons-material/Badge';
import CategoryIcon from '@mui/icons-material/Category';
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import SecurityIcon from '@mui/icons-material/Security';
import StoreIcon from '@mui/icons-material/Store';
import WallpaperIcon from '@mui/icons-material/Wallpaper';
import { SvgIcon } from '@mui/material';
import { suId } from 'src/utils/util';

export const items = (user) =>{

  return [
    {
      title: 'Dashboard',
      path: '/',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <ChartBarIcon />
        </SvgIcon>
      )
    },
    {
      title: 'Retailers',
      path: '/customers',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <UsersIcon />
        </SvgIcon>
      )
    },
    {
      title: 'Wholesaler',
      path: '/wholesalers',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <ShoppingBagIcon />
        </SvgIcon>
      )
    },
  
    {
      title: 'Staffs',
      path: '/staffs',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <BadgeIcon />
        </SvgIcon>
      )
    },

      
    {
      title: 'Admins',
      path: '/users/SA',
      show : !!user ? user.id == suId : false ,
      icon: (
        <SvgIcon fontSize="small">
          <AdminPanelSettingsIcon />
        </SvgIcon>
      )
    },
    {
      title: 'Profile',
      path: '/account',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <UserIcon />
        </SvgIcon>
      )
    },
  
    {
      title: 'Stores',
      path: '/stores',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <StoreIcon />
        </SvgIcon>
      )
    },
  
    {
      title: 'Add User',
      path: '/users/create/R',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <PersonAddAltIcon />
        </SvgIcon>
      )
    },

    {
      title: 'Remove Background',
      path: '/removebg',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <WallpaperIcon />
        </SvgIcon>
      )
    },

    {
      title: 'Item Categories',
      path: '/item/category/',
      show : !!user ? user.userType == 'SA' : false,
      icon: (
        <SvgIcon fontSize="small">
          <CategoryIcon />
        </SvgIcon>
      )
    },


    {
      title: 'Store Categories',
      path: '/store/category/',
      show : !!user ? user.userType == 'SA' : false,
      icon: (
        <SvgIcon fontSize="small">
          <StoreIcon />
        </SvgIcon>
      )
    },

    {
      title: 'Groups',
      path: '/groups',
      show : !!user ? user.userType == 'SA' : false,
      icon: (
        <SvgIcon fontSize="small">
          <SecurityIcon />
        </SvgIcon>
      )
    },
  
    {
      title: 'Settings',
      path: '/settings',
      show : true,
      icon: (
        <SvgIcon fontSize="small">
          <CogIcon />
        </SvgIcon>
      )
    },
  
  
    // {
    //   title: 'Login',
    //   path: '/auth/login',
    //   icon: (
    //     <SvgIcon fontSize="small">
    //       <LockClosedIcon />
    //     </SvgIcon>
    //   )
    // },
    // {
    //   title: 'Register',
    //   path: '/auth/register',
    //   icon: (
    //     <SvgIcon fontSize="small">
    //       <UserPlusIcon />
    //     </SvgIcon>
    //   )
    // },
    // {
    //   title: 'Error',
    //   path: '/404',
    //   icon: (
    //     <SvgIcon fontSize="small">
    //       <XCircleIcon />
    //     </SvgIcon>
    //   )
    // }
  ];
}

