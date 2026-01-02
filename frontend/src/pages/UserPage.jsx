import React from 'react'
import UserSidebar from './UserSidebar';
import { Outlet } from 'react-router-dom';

function UserPage() {
  return (
    <div style={{display: 'flex', flexDirection: 'row', width: '100vw', margin: 0, padding: '0px 30px'}}>
      <UserSidebar />
      <div style={{ display: 'flex', overflow: 'auto', width: '100vw', margin: 0, padding: 0}}>
        <Outlet />
      </div>
    </div>
  )
}

export default UserPage;