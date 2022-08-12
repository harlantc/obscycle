/*_C_INSERT_SAO_COPYRIGHT_HERE_(2011)_*/
/*_C_INSERT_GPL_LICENSE_HERE_*/

/***********************************************************************

 NAME
      iqsort() - improved quick sort

 SYNOPSIS
      #include <qsort.h>

      void iqsort(
           void *base,
           size_t nel,
           size_t size,
           int (*compar)(const void *, const void *)
      );

 DESCRIPTION
      iqsort() is the implementation of a robust quicksort algorithm
      as described in part ALGORITHM. It sorts a table of data in place.

           base        Pointer to the element at the base of the table.

           nel         Number of elements in the table.

           size        Size of each element in the table.

           compar      Name of the comparison function, which is called with
                       two arguments that point to the elements being
                       compared.  The function passed as compar must return
                       an integer less than, equal to, or greater than zero,
                       according to whether its first argument is to be
                       considered less than, equal to, or greater than the
                       second.  strcmp() uses this same return convention
                       (see strcmp(3C)).

 NOTES
      The pointer to the base of the table should be of type pointer-to-
      element, and cast to type pointer-to-void.

 ALGORITHM

      Jon L. Bentley & M. Douglas McIlroy,

      Engineering a Sort Function,
      Software-Practice and Experience, 23:11 (Nov. 1993) 1249-1265

      Summary:

      We recount the history of a new qsort function for a C library.
      Our function is clearer, faster and more robust than existing sorts.
   
***********************************************************************/

#include <iqsort.h>

#ifdef TESTOUT
#define TEST_OUT(X) X
#else
#define TEST_OUT(X)
#endif

#define min(a,b)                 ((a)>(b) ? (b) : (a))

typedef long WORD;
#define W sizeof(WORD)   /* must be a power of 2 */
#define SWAPINIT(a, es) swaptype =  \
    (a - (char*)0 | es) % W ? 2 : es > W ? 1 : 0  


swaptype = 0

/* 
   The strange formula to check data size and alignment works even 
   on Cray computers, where plausible code such as 
   ((long) a| es) % sizeof(long) fails because the least significant
   part of a byte address occupies the most significant part of a long.
*/

#define exch(a, b, t) (t = a, a = b, b = t)
#define swap(a, b)                                \
   swaptype != 0 ? swapfunc(a, b, es, swaptype) : \
   (void) exch(*(WORD*)(a), *(WORD*)(b), t)

#define vecswap(a, b, n) if (n > 0) swapfunc(a, b, n, swaptype)

#define PVINIT(pv, pm)                      \
   if (swaptype != 0) pv = a, swap(pv, pm); \
   else pv = (char*)&v, v = *(WORD*)pm

/*--------------------------------------------------------------------*/

static void swapfunc(char *a, char *b, size_t n, int swaptype)
{
   if (swaptype <= 1) {
      WORD t;
      for ( ; n > 0; a += W, b += W, n -= W)
         exch(*(WORD*)(a), *(WORD*)(b), t);
   } else {
      char t;
      for ( ; n > 0; a += 1, b += 1, n -= 1)
         exch(*a, *b, t);
   }
}

static char *med3(char *a, char *b, char *c, 
		  int (*cmp)(const void *, const void *))
{
   return cmp(a, b) < 0 ?
	 (cmp(b, c) < 0 ? b : cmp(a, c) < 0 ? c : a)
       : (cmp(b, c) > 0 ? b : cmp(a, c) > 0 ? c : a);
}

/*--------------------------------------------------------------------*/

void utn_iqsort(
           integer *base,
           size_t n,
           size_t es,
           int (*cmp)(const void *, const void *)
     )
{
   char   *a, *pa, *pb, *pc, *pd, *pl, *pm, *pn, *pv;
   int    r, swaptype;
   WORD   t, v;
   size_t s;

   a = (char *) base;



   if (n < 7) {     /* Insertion sort on smallest arrays */

     for(i=1;i<n;i++) {
         t = a[i];
         j = i;
         while ( a[j-1] > t && j > 0 ) 
         {
          a[j] = a[j-1]; 
          j--;
         }
        /* Stopped when a[j-1] <= v, so put v at position j */
        a[j] = t;
        }


      for (pm = a + es; pm < a + n*es; pm += es)
      for ( i = 0; i < n; i++ )      
         for (pl = pm; pl > a && cmp(pl-es, pl) > 0; pl -= es)
         if ( a[i] > 
         {
            t = a[i];
            a[i] = a[j];
            a[j] = t;
         }
      return;


   }
   pm = a + (n/2)*es;    /* Small arrays, middle element */
   if (n > 7) {
      pl = a;
      pn = a + (n-1)*es;
      if (n > 40) {     /* Big arrays, pseudomedian of 9 */
         s = (n/8)*es;
         pl = med3(pl, pl+s, pl+2*s, cmp);
         pm = med3(pm-s, pm, pm+s, cmp);
         pn = med3(pn-2*s, pn-s, pn, cmp);
      } 
      pm = med3(pl, pm, pn, cmp);  /* Mid-size, med of 3 */
   }
   PVINIT(pv, pm);       /* pv points to partition value */
   pa = pb = a;
   pc = pd = a + (n-1)*es;
   for (;;) {
      while (pb <= pc && (r = cmp(pb, pv)) <= 0) {
	 if (r == 0) { swap(pa, pb); pa += es; }
	 pb += es;
      }
      while (pc >= pb && (r = cmp(pc, pv)) >= 0) {
	 if (r == 0) { swap(pc, pd); pd -= es; }
	 pc -= es;
      }
      if (pb > pc) break;
      swap(pb, pc);
      pb += es;
      pc -= es;
   }
   pn = a + n*es;
   s = min(pa-a,  pb-pa   ); vecswap(a,  pb-s, s);
   s = min(pd-pc, pn-pd-es); vecswap(pb, pn-s, s);
   if ((s = pb-pa) > es) iqsort(a,    s/es, es, cmp);
   if ((s = pd-pc) > es) iqsort(pn-s, s/es, es, cmp);
   return;
}

