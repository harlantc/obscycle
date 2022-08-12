package info;
/*
  Copyrights:

  Copyright (c) 2000-2014 Smithsonian Astrophysical Observatory

  Permission to use, copy, modify, distribute, and  sell  this
  software  and  its  documentation  for any purpose is hereby
  granted without  fee,  provided  that  the  above  copyright
  notice  appear  in  all  copies and that both that copyright
  notice and this permission notice appear in supporting docu-
  mentation,  and  that  the  name  of the  Smithsonian Astro-
  physical Observatory not be used in advertising or publicity
  pertaining to distribution of the software without specific,
  written  prior  permission.   The Smithsonian  Astrophysical
  Observatory makes no representations about  the  suitability
  of  this  software for any purpose.  It is provided  "as is"
  without express or implied warranty.
  THE  SMITHSONIAN  INSTITUTION  AND  THE  SMITHSONIAN  ASTRO-
  PHYSICAL OBSERVATORY DISCLAIM ALL WARRANTIES  WITH REGARD TO
  THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF MERCHANT-
  ABILITY AND FITNESS,  IN  NO  EVENT  SHALL  THE  SMITHSONIAN
  INSTITUTION AND/OR THE SMITHSONIAN ASTROPHYSICAL OBSERVATORY
  BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES
  OR ANY DAMAGES  WHATSOEVER  RESULTING FROM LOSS OF USE, DATA
  OR PROFITS,  WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
  OTHER TORTIOUS ACTION,  ARISING OUT OF OR IN CONNECTION WITH
  THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/******************************************************************************/

import java.lang.String;
import java.lang.Integer;
import java.util.Vector;
import info.Observation;


/**
   The Observation List class contains a vector of observations
 */
public class ObservationList extends Vector<Observation>
{



    public ObservationList() {
	init();
    }

    private void init() {
    }

/**
  * getByObsid  - returns a single Observation record as specified by 
  *               the observation id
  * @param obsid - observation id 
  * @return Observation - matching Observation record or null
  */
    public Observation getByObsid( Integer obsid)
    {
      Observation obs;
      Observation obsMatch = null;
      for (int ii=0; ii< this.size() ; ii++) {
        obs = (Observation)this.get(ii);
        if (obs != null && obs.getObsid().intValue() == obsid.intValue()) {
           obsMatch = obs;
           break;
        }
      }

      return obsMatch;
    }



    public int countOpenObsids()
    {
      int retval = 0;
      Observation obs;
      for (int ii=0; ii< this.size() ; ii++) {
        obs = (Observation)this.get(ii);
        if (obs != null && obs.isUnobserved()) {
           retval += 1;
        }
      }
      return retval;
    }


}
