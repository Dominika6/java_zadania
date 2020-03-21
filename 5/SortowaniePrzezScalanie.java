public class SortowaniePrzezScalanie extends Thread{                     //sortowanie przez scalanie przy użyciu wątków

    
    private final int left;
    private final int right;
    private int[] result;
    private final int[] tab;
    
    
    @Override
    public void run(){
        //System.out.println("Dzielimy na : (" + this.left + ", " + this.right + ") ");
        dziel();
    }
    
    
    public SortowaniePrzezScalanie(int[] tab, int i, int j){
        this.left  = i;
        this.right = j;
        this.tab = tab;
    }
   
    
    private void dziel(){

        if(left == right && left >= 0){
            this.result = new int[]{ tab[left] };
            return;
        }
        if(left > right) return;

        int prawy = this.left + (right-left) / 2;                                           //'prawy' i 'lewy' (indeksy w tablicy początkowej 'tab' tworzą nowe przedziały

        SortowaniePrzezScalanie nowyLewy = new SortowaniePrzezScalanie(tab, left, prawy);   //tworzymy nowy wątek dla nowego - lewego przedziału
        Thread t1 = new Thread(nowyLewy);
        t1.start();

        int lewy = 1 + prawy;            

        SortowaniePrzezScalanie nowyPrawy= new SortowaniePrzezScalanie(tab, lewy, right);   // i analogicznie dla prawego
        Thread t2 = new Thread(nowyPrawy);
        t2.start();

        try{
            t1.join();                                                                      //join() - Waits for this thread to die. ~Oracle
            t2.join();
        }catch (InterruptedException e){
            System.out.println(e.getMessage());
        }
        scal(nowyLewy.result,nowyPrawy.result);
    }


    private  int[] scal(int[] left, int[] right){
        result = new int[left.length + right.length];

        int x = 0;
        int i = 0;
        int j = 0;
        
        while(i < left.length && j < right.length){   
            if(i < left.length && j < right.length && left[i] < right[j]){
                result[x++] = left[i++];
            }

            else if(j < right.length && i < left.length && right[j] < left[i]){
                result[x++] = right[j++];
            }
        }

        while(i < left.length){
            result[x++] = left[i++];
        }

        while(j < right.length){
            result[x++] = right[j++];
        }        
        return result;
    }
    
    
    public static void main(String[] args){

        int tab[] ={3, 6, 4, 2, 1, 9};
        System.out.println("Tablica przed sortowaniem: ");
        for(int i=0;i<tab.length;i++){
            System.out.print(tab[i] + "  ");
        }
        System.out.println("\n");
        
        SortowaniePrzezScalanie tablica = new SortowaniePrzezScalanie(tab, 0, tab.length - 1);

        Thread watek = new Thread(tablica);
        watek.start();                                          // start() - Causes this thread to begin execution; 
                                                                // the Java Virtual Machine calls the run method of this thread. ~Oracle

        try{
            watek.join();                                       //join() - Waits for this thread to die. ~Oracle
        }catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Tablica po sortowaniu: ");
        for(int i: tablica.result){
            System.out.print(i + "  ");
        }
    }
}
