/*
 * Copyright (C) 2014 Diganth Aswath <diganth2004@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package soapUIScripts

/**
 *
 * @author Diganth Aswath <diganth2004@gmail.com>
 */
class date {
    def today, todayDate, todayTime;
        def date(){
            today = new Date();
        }
        def today(){
            return today.toString();
        }
        def todayDate(){
            //today = new Date();
            return today.getDateString().split('/').join('_');
        }
        def todayTime(){
            //today = new Date();
            return today.getTimeString().split(':').join('_');
        }
}

