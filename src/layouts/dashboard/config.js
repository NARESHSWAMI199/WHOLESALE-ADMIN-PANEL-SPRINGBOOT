import ChartBarIcon from '@heroicons/react/24/solid/ChartBarIcon';
import CogIcon from '@heroicons/react/24/solid/CogIcon';
import ShoppingBagIcon from '@heroicons/react/24/solid/ShoppingBagIcon';
import UserIcon from '@heroicons/react/24/solid/UserIcon';
import UsersIcon from '@heroicons/react/24/solid/UsersIcon';
import { StoreOutlined } from '@mui/icons-material';
import { SvgIcon } from '@mui/material';
import StoreIcon from '@mui/icons-material/Store';
import WorkspacePremium from '@mui/icons-material/WorkspacePremium';
import { useAuth } from 'src/hooks/use-auth';
import PersonAddAltIcon from '@mui/icons-material/PersonAddAlt';
import SecurityIcon from '@mui/icons-material/Security';
import BadgeIcon from '@mui/icons-material/Badge';
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
      title: 'Groups',
      path: '/groups',
      show : !!user ? user.userType =="A" || user.userType == 'SA' : false,
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

