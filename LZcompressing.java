/*ιμπραχιμι ελτον Π11049.
Υλοποιουνται και οι δυο μεθοδοι σε επιπεδο bits.Γινετε δημιουργια ολων των αρχειων(συμπιεσμενα,αποσυμπιεστα των 77,78) για ελεγχο.
Παρεχεται εμφανιση του αποτελεσματος στην εκτελεση αλλα δεν ειναι ακριβης οποτε καλυτερα να ελεγχεται το αρχειο.

Στον 77 γινετε ο ελεγχος για γεμισμα του stringbuilder(παρολο που δεν εγινε εξακριβωση λογο υπερβολικα μεγαλου μεγεθους και χρονου)
Στον 77 εννοειτε οτι οσο για την συμπιεση τοσο για την αποσυμπιεση ειναι απαραιτητες οι παραμετρους εισαγωγης του χρηστη,
αυστηρα ιδιες και στην συμπιεση και στην αποσυμπιεση.

Στον 78 δεν γινετε ελεγχος γεμισματος του dictionary,θα επρεπε να περιορισω το μεγεθος του dictionary,
ΣΥΜΒΑΣΗ: Στον 78 υποθετουμε οτι η μνημη και οτι οι 2^31-1 εγγραφες ενος arraylist(dictionary) ειναι αρκετες για το κειμενο!!!!!!!!!!
Επισης παρολο που στον 78 ενας περιορισμος μεγεθους αναλογα με το κειμενο(π.χ. repeatedTxt.txt),σε καποιεσ περιπτωσεις, θα μπορουσε να αποδοση καλυτερα
προτιμησα να το αφησω απλο.

Συνθηκες οπως  bitSet.set(i,false),καποια bitset.clear(),και καποια else εχουν μπει για παραπανω προσωπικη 
ασφαλεια παρολο που οι περισσοτερες πιθανοτατα ειναι απλος πλεονασμος. 

Η κωδικοποιηση επιτευχθηκε με χρηση bitset και παραμετρους ελεγχου απαραιτητων Bits τις παραμετρους που εισαγει
ο χρηστης και το μεγεθος του παραθυρου αναζητησης, μεχρι να μεγιστοποιηθει η να βρεθει το επιθυμητο μεγεθος(στην περιπτωση lookWindowLength)
που εισηγαγε ο χρηστης.Η εγγραφη γινετε σε bytes,bits απο το bitset  μεγεθους πολλαπλασιο του 8,και το υπολοιπο καταχωρειτε σε αλλο bitset
για κωδικοποιηση με την επομενη εγγραφη.*/

package lzcompressing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class LZcompressing implements Serializable{
static Scanner read=new Scanner(System.in);
static File file=new File("mediumTxt.txt");      
static File compressedFile;
static File uncompressedFile;
static FileOutputStream writeBytes;
static DataInputStream readBytes;
static BufferedReader readFile;
static BufferedWriter writeFile;
static String nextCharStr;  
static  String path="";
static char Char;                                                      
static int matchLocation;                                                
static int srcWindowLength;                                           
static int lookWindowLength ;                                                                                              
static int CharinInt;
static int totalLO;
static int matchCounter;
static int offsetCounter;
static int totalBits;
static boolean existsLO;
static byte[] b;
static StringBuilder Text;
static BitSet leftover;
static BitSet code;      

static int bitCount(int value)  //καταμετρηση των απαραιτητων bit για την αναπαρασταση ενος αριθμου value
{
    return Integer.SIZE-Integer.numberOfLeadingZeros(value); 
}

static int totalNumber(int total) // επιστρεφει τον max αριθμο που αναπαριστατε με total bits
{ 
    int number=0;
    for(int i=0;i<total;i++)
    {
        number+=Math.pow ( 2,i );
    }
    return number;   
 }

static BitSet cloneLeftOver(int index,BitSet to,BitSet from,int total)//αντιγραφη των bits απο ενα bitset σε αλλο σε συγκεκριμενο start-end
{
    int start=0;
    int counter=0;
    if(index!=0)
    start=index;
    for(int i=start;i<total;i++)
    {
        if(from.get(i))
            to.set(counter,true);
        else
            to.set(counter,false);
        counter++;
    }  
   return to;
}

static BitSet setBits(int maxBits,int number,int totalStored,BitSet code)//καταχωρηση Int σε bitset
{
    int count=0;
    for(int i=totalStored;i<totalStored+maxBits;i++)
    {
        if((number>>count & 1)==1)
            code.set(i,true);
        else
            code.set(i,false);
        count++;
    }
    return code;
}

static int count(int match,int max,String target)//υπολογισμος του μεγιστου match καθε φορα
{
    if(match<max)
        match=target.length();
                                   
    if(match>max)
        match=max;
                                 //μικρος πλεονασμος ασφαλειας(οχι else)
    if(match==0)                       
        match=1;

    return match;
}

static BitSet fromByteArray(BitSet code,byte[] b,int index,int total,int start)//απο bytearray σε bitset σε συγκεκριμενες θεσεις,και συγκ. bits
{
    int counter=0;
    int match=start/8;
    int j =0;
 
    for(int i=start;i<total;i++)
    {
        if(counter==8)
            { counter=0; match++;}
        if((b[match] & (1<<(i%8)))>0)
            code.set(index+j,true);
        else
            code.set(index+j,false);
        counter++;
        j++;
    }  
    return code;
}

static int setCode(int maxBits,int totalUsed,BitSet code)//απο Bitset σε number 
{
    int number=0;
    for(int i=totalUsed+maxBits-1;i>=totalUsed;i--)
    {
       
        if(code.get(i))
            number=(number | (1<<i-totalUsed));
    }
    if(number<0)
        number=number & totalNumber(maxBits);

    return number;
}

/*----------------ΣΥΜΠΙΕΣΗ LZ77-----------------------*/
public static void lz77compress() throws IOException
{  
    try 
    {            
   
     
        System.out.println("LZ77 TEXT COMPRESSING.....");
        compressedFile=new File(path+"LZ77compressed.txt");
        compressedFile.delete();
        compressedFile=new File(path+"LZ77compressed.txt");  
        
        System.out.println("Compressing the text "+file.getName()+" in path "+file.getAbsolutePath()+" into LZ77compressed.txt...");
        Text = new StringBuilder() ;
        int charCount=0;
        readFile=new BufferedReader(new FileReader(file));           
        writeBytes=new FileOutputStream(compressedFile,true);
        int srcWindowStart;                                                                         
        String Line;
        String searchWindowString;                                      
        String searchTarget;  
        totalLO=0;
        matchCounter=0;
        offsetCounter=0;
        leftover=new BitSet();
        existsLO=false;      
        boolean indexIssue=false;
        int matchedLength;                                       
        int offset;   
        
        while((Line=readFile.readLine())!=null)
        { 
            if((Text.length()+Line.length())<=(Math.pow(2,31)-1))//ελεγχος μηπως φτασει το μεγιστο ο stringbuilder
                Text.append(Line);              //καταχωρηση του κειμενου προς συμπιεση σε ενα stringbuilder
            else
            {
                
                indexIssue=true; 
                break;
            }
        }
        try
        {
            while(charCount<Text.length())       //οσο υπαρχουν ακομα χαρακτηρες προς συμπιεση
            {     
                                                            
            
                if(charCount-srcWindowLength>=0)                //Αν εχει γινει καταμετρηση περα απο το μεγεθος του παραθυρου
                    srcWindowStart=charCount-srcWindowLength;  //κυλιση παραθυρου
                else
                    srcWindowStart=0;                          //αρχικοποιηση παραθυρου,θεση 0 στο searchWindowString
                
                if(charCount==0)                                                    //<---αρχικοποιηση και fix κειμενου παραθυρου αναζητησης---->
                    searchWindowString="";                                          
                else    
                    searchWindowString=Text.substring(srcWindowStart,charCount);
                                                                            //<---διαδικασια ευρεσης χαρακτηρων και προσαυξησης κωδικοποιημενου κειμενου---->
                matchedLength=1;                                                       //μεγεθος του searchTarget..δηλαδη το συνολο των χαρακτηρων προς κωδικοπ.
                searchTarget=Text.substring(charCount,charCount+matchedLength);//χαρακτηρας προς κωδικοποιηση
                if(searchWindowString.contains(searchTarget))                         //ελεγχος παραθυρου αναζητησης αν υπαρχει ο  χαρακτηρας 
                {
                    matchedLength++;                                                 //Αν υπαρχει τοτε προσαυξηση κατα ενα του κωδικοποιημενου μεγεθους
                    while(matchedLength<=lookWindowLength)
                    {                   
                        searchTarget=Text.substring(charCount,charCount+matchedLength);      //καταχωρηση και επομενου χαρακτηρα 
                        matchLocation=searchWindowString.indexOf(searchTarget);                     //ελεγχος παραθυρου αναζητησης αν υπαρχει η σειρα χαρακτηρων 
                        if((matchLocation!=-1) && (charCount+matchedLength<Text.length()))   //επιστρεφει -1 το index αν δεν υπαρχει.  
                            matchedLength++;                                                         //αν υπαρχει και δεν εχουμε βρεθει στο τελος του κειμενου
                        else                                                                         //συνεχισε να αυξανεις
                            break;                                           
                    }  
                    matchedLength--;                                                                //μειωση κατα 1 για επαναφορα στο σωστο μεγεθος
                    matchLocation=searchWindowString.indexOf(Text.substring(charCount,charCount+matchedLength));     //index κωδικοποιημενων χαρακτηρων(τελος)
                    charCount+=matchedLength;                                                          //αυξηση του counter χαρακτηρων κατα "κωδικοποιημενο μεγεθος"
                    if(charCount<(srcWindowLength+matchedLength))                               //Αν το παραθυρο αναζητησης δεν εχει φτασει ακομα το μεγιστο μεγεθος
                        offset=charCount-matchLocation-matchedLength;                    
                    else                                                                             //προσδιορισμος  της αποκλισης 
                        offset=srcWindowLength-matchLocation;                                              
                   
                    nextCharStr=Text.substring(charCount,charCount+1);                   //καταχωρηση του επομενου χαρακτηρα,δηλδαδη αυτου που δεν "ταιριαξε" 
                    Char=nextCharStr.charAt(0);                                
                }
                else                                                     //Η περιπτωση που ο πρωτος χαρακτηρας προς κωδικοπ. δεν υπαρχει στο παραθυρο αναζητησης 
                { 
                    nextCharStr=Text.substring(charCount,charCount+1); 
                    Char=nextCharStr.charAt(0);
                    offset=0;                                                                
                    matchedLength=0;
                }
                totalBits=0;                                                    //<--Κωδικοποιηση της εγγραφης σε Bits με bitset,μονο τα απαραιτητα Bits
                matchCounter=count(matchCounter,lookWindowLength,searchWindowString);
                offsetCounter=count(offsetCounter,srcWindowLength,searchWindowString);
                int matchBits=bitCount(matchCounter);                           // Bits που πρεπει να κωδικοποιηθουν απο καθε τιμη
                int offsetBits=bitCount(offsetCounter);
                if(existsLO)                                                    //μεταφορα υπολοιπου απο την προηγουμενη εγγραφη
                {
                    code=new BitSet(matchBits+offsetBits+8+leftover.length());
                    code=cloneLeftOver(0,code,leftover,totalLO);
                    existsLO=false;
                    totalBits+=totalLO;
                    totalLO=0;
                    leftover.clear();
                }
                else
                   code=new BitSet(matchBits+offsetBits+8);  
                
                code=setBits(matchBits,matchedLength,totalBits,code);           //εισαγωγη των bits απο καθε τιμη στο Bitset στην καταλληλη θεση
                totalBits+=matchBits;
                code=setBits(offsetBits,offset,totalBits,code);
                totalBits+=offsetBits;
                code=setBits(8,(int)Char,totalBits,code);
                totalBits+=8;
               
                if(totalBits%8==0)                                              //Αν δεν υπαρχει υπολοιπο, κατευθειαν εγγραφη στο αρχειο σε bytes
                {                                                               
                    b=code.toByteArray();
                    writeBytes.write(b);
                    code.clear();
                    if(b.length<totalBits/8)  
                        for(int i=0;i<totalBits/8-b.length;i++)
                            writeBytes.write((byte)0);                   
                }
                else                                                            //αλλιως εγγραφη μονο των bits που χωρανε σε bytes  
                {                                                               //και το υπολοιπο εισαγωγη σε Bitset για εγγραφη με την επομενη
                    if(totalBits>=8)                                            //κωδικοποιηση
                    {
                        int toWrite=totalBits/8;
                        b=new byte[toWrite];
                        BitSet towrite=new BitSet();
                        towrite=cloneLeftOver(0,towrite,code,toWrite*8);
                        b=towrite.toByteArray();
                        writeBytes.write(b);
                        if(b.length<toWrite)
                            for(int i=0;i<toWrite-b.length;i++)
                                writeBytes.write((byte)0);
                             
                        code.clear(0,(toWrite*8)-1);
                        totalLO=totalBits%8;
                        leftover=cloneLeftOver(toWrite*8,leftover,code,totalBits);  
                        code.clear();
                        existsLO=true;
                    }
                    else                      //αχρησιμοποιητη συνθηκη,προεραιτικη για ασφαλεια
                    {
                        
                        leftover=cloneLeftOver(0,leftover,code,totalBits);
                        totalLO=totalBits%8;
                        code.clear();
                        existsLO=true;
                    }   
                }                                                                                                
                charCount++; 
                if(indexIssue && charCount>=Text.length())         //Αν το κειμενο ειναι μεγαλυτερο του 2^31-1,διαβασμα και του υπολοιπου στον stringbuilder
                {
                    charCount=srcWindowLength+lookWindowLength;
                    Text.delete(0,Text.length()-(srcWindowLength+lookWindowLength));
                    Text.append(Line);
                    indexIssue=false;
                    while((Line=readFile.readLine())!=null)
                    {  
                        if((Text.length()+Line.length())<=(Math.pow(2,31)-1))
                            Text.append(Line);              
                        else
                        { 
                            indexIssue=true; 
                            break;
                        }
                    }
                } 
            }
        }catch( StringIndexOutOfBoundsException e ) {}  //Μπορει να ποιασει στο τελος του κειμενου μονο,οπου το catch δεν κανει τιποτα,
            finally
            {                                       
                if(existsLO)//ελεγχος υπαρξης καποιου υπολοιπου κατα την τελευταια εγγραφη 
               {
                b=leftover.toByteArray();
                writeBytes.write(b);
                leftover.clear();
                existsLO=false;
               }
               writeBytes.flush();
               writeBytes.close();
               readFile.close(); 
               System.out.println("Compressed approximately in : "+compressedFile.length()+" from "+Text.length()+" bytes with LZ77...");
               Text.delete(0,Text.length());
            }  
    }
    catch ( IOException ex )  
    {
        Logger.getLogger(LZcompressing.class.getName()).log(Level.SEVERE, null, ex);
    }    
}   
        
/*----------------ΑΠΟΣΥΜΠΙΕΣΗ-----------------------*/
public static void lz77decompress() throws IOException
{
   
    try 
    {
        uncompressedFile=new File(path+"LZ77uncompressed.txt");
        uncompressedFile.delete();
        compressedFile=new File(path+"LZ77compressed.txt"); 
        uncompressedFile=new File(path+"LZ77uncompressed.txt");
        System.out.println("LZ77 TEXT DECOMPRESSING.....");
        System.out.println("Decompressing the text "+compressedFile.getName()+" in path "+compressedFile.getAbsolutePath()+" into LZ77uncompressed.txt...");
        writeFile=new BufferedWriter(new FileWriter(uncompressedFile,true));
        readBytes=new DataInputStream(new FileInputStream(compressedFile));
        Text= new StringBuilder ();                                      
        
        totalLO=0;
        matchCounter=0;
        offsetCounter=0;
        leftover=new BitSet();
        existsLO=false;
        int matchedLength;                                       
        int offset; 
        while (readBytes.available()!=0)//διαβαζει Byτεs αναλογα με τις παραμετρους χρηστη η το μεγεθος του παραθυρου 
        {                              //χρησιμοποιητε το παραθυρο αναζητησης για να καθοριζονται τα απαραιτητα bits!!!
            
            totalBits=0;
            matchCounter=count(matchCounter,lookWindowLength,Text.toString());
            offsetCounter=count(offsetCounter,srcWindowLength,Text.toString());
            int matchBits=bitCount(matchCounter);
            int offsetBits=bitCount(offsetCounter);
            code=new BitSet(matchBits+offsetBits+8);
            totalBits=matchBits+offsetBits+8;
            if(existsLO)                                                        //ιδια διαδικασια με της κωδικοποιησης       
            {
                code=cloneLeftOver(0,code,leftover,totalLO);
                existsLO=false;
                totalBits-=totalLO;
                leftover.clear();  
            }
            if(totalBits%8==0)
            {
                b=new byte[totalBits/8];
                readBytes.read(b);
                code=fromByteArray(code,b,matchBits+offsetBits+8-totalBits,totalBits,0);
            }
            else
            {
                b=new byte[(totalBits/8)+1];                                    //με την διαφορα οτι τραβαει ενα παραπανω byte,οταν υπαρχει υπολοιπο
                readBytes.read(b);                                              //και το υπολοιπο αυτο των Bits ανηκει στην επομενη εγγραφη
                code=fromByteArray(code,b,matchBits+offsetBits+8-totalBits,totalBits,0);
                leftover=fromByteArray(leftover,b,0,b.length*8,b.length*8-(b.length*8-totalBits));
                totalLO=b.length*8-totalBits;
                existsLO=true;    
            }
            CharinInt=0;
            int totalUsed=0;
            matchedLength=setCode(matchBits,totalUsed,code);                    //καθορισμος της εγγραφης που διαβαστηκε στις αντιστοιχες
            totalUsed+=matchBits;                                               //μεταβλητες απο το bitset
            offset=setCode(offsetBits,totalUsed,code);
            totalUsed+=offsetBits;
            CharinInt=setCode(8,totalUsed,code);
            totalUsed+=8;
            Char=(char)(CharinInt & totalNumber(8));
            code.clear();
            if(matchedLength==0)                                                // καταχωρηση στο παραθυρο αναζητησης
            {
                if(Text.length()+8>=(Math.pow(2,31)-1))                         //ελεγχος μηπως ο stringbuilder γεμισε 
                { 
                    writeFile.write(Text.substring(0,Text.length()-(srcWindowLength+lookWindowLength)));  //εγγραφη του και καθαρισμα
                    Text.delete(0,Text.length()-(srcWindowLength+lookWindowLength));
                }
                Text.append(Char); 
            }
            else                                                                //ιδια λογικη 
            {
                if(Text.length()+(Text.substring(Text.length()-offset,(Text.length()-offset)+matchedLength)).length()+8>=(Math.pow(2,31)-1))
                {
                    writeFile.write(Text.substring(0,Text.length()-(srcWindowLength+lookWindowLength)));
                    Text.delete(0,Text.length()-(srcWindowLength+lookWindowLength));
                }
                 
               Text.append(Text.substring(Text.length()-offset,(Text.length()-offset)+matchedLength));
               Text.append(Char);
            }
        }
        writeFile.write(Text.toString());//Εγγραφη του πλεον πληρες stringBuffer στο αρχειο
        Text.delete(0,Text.length());
        readBytes.close();
        writeFile.flush();
        writeFile.close(); 
    } 
    catch (FileNotFoundException ex) 
    {
        Logger.getLogger(LZcompressing.class.getName()).log(Level.SEVERE, null, ex);
    }         
}                                                   


/*----------------ΣΥΜΠΙΕΣΗ LZ78-----------------------*/
public static void lz78compress() throws IOException
{  
    try 
    {
        compressedFile=new File(path+"LZ78compressed.txt");
        compressedFile.delete();
        System.out.println("LZ78 TEXT COMPRESSING.....");
        compressedFile=new File(path+"LZ78compressed.txt");
        System.out.println("Compressing the text "+file.getName()+" in path "+file.getAbsolutePath()+" into LZ78compressed.txt...");
        readFile=new BufferedReader(new FileReader(file));        
        writeBytes=new FileOutputStream(compressedFile,true);
        ArrayList<String> Dictionary=new ArrayList<>();                         //arraylist για αναπαρασταση dictionary
        String Line;
        int indexBits;
        int charCount=0;
        boolean end=false;
        Dictionary.add(null);
        leftover=new BitSet();
        existsLO=false;
        totalLO=0;
 
        while((Line=readFile.readLine())!=null)                                 //διαβασμα ανα γραμμη
        { 
            int j=0;                                                            //αντιστοιχη στο matchedLength
            charCount+=Line.length();
            for(int i=0;i<Line.length() && end==false;i=j )                      
            {                
                indexBits=bitCount(Dictionary.size());                          //το  συνολο bits στο οποιο θα κωδικοποιηθη το index 
                totalBits=0;
                if(existsLO)
                {
                    code=new BitSet(indexBits+8+totalLO);
                    code=cloneLeftOver(0,code,leftover,totalLO);
                    totalBits+=totalLO;
                    existsLO=false;
                    totalLO=0;
                    leftover.clear();
                }
                else
                   code=new BitSet(indexBits+8);
               
                j=i+1;
                if(Dictionary.contains(Line.substring(i,j)))                    //Συνθηκη ελεγχου υπαρξης η στο Line
                {
                 try{
                        while(Dictionary.contains(Line.substring(i,j)))         
                            j++;
                    }catch(StringIndexOutOfBoundsException ex)
                        {j--; end=true;}                                        //Ιδια μεθοδολογια με τον 77 για την κωδικοποιηση των bits
                    if(Line.substring(i,j).length()<=Line.length())             //αχρειαστη συνθηκη,ασφαλειας
                    {                                                           //καθορισμος του bitset προς κωδικοποιηση  
                        if(end)                                                 //Αν εχουμε βρεθει στο τελος του Line πριν τον ελεγχο στην ανωτερη for
                        {
                            String Last;
                            if((Last=readFile.readLine())==null)
                            {
                                
                                code=setBits(indexBits,Dictionary.indexOf(Line.substring(i,j)),totalBits,code);
                                totalBits+= indexBits;
                                nextCharStr=" ";
                                code=setBits(8,(int)nextCharStr.charAt(0),totalBits,code);
                                totalBits+=8;
                            }
                            else 
                            {
                                Line=Line.substring(i,j)+Last;
                                end=false;
                                j=0;
                                charCount+=Last.length();
                            }
                        }
                        else
                        {  
                            code=setBits(indexBits,Dictionary.indexOf(Line.substring(i,j-1)),totalBits,code);
                            totalBits+=indexBits;
                            code=setBits(8,(int)Line.substring(j-1,j).charAt(0),totalBits,code);
                            totalBits+=8;                          
                            Dictionary.add(Line.substring(i,j));
                        }
                    }  
                }
               else
                {                  
                    code=setBits(indexBits,0,totalBits,code);
                    totalBits+=indexBits;
                    code=setBits(8,(int)Line.substring(i,i+1).charAt(0),totalBits,code);
                    totalBits+=8;               
                    Dictionary.add(Line.substring(i,i+1));     
                }                                                               //Καθορισμος των απαραιτων Bytes για την εγγραφη του bitset
                if(totalBits%8==0)                                              //Kαι καθορισμος του πιθανου leftover,kαι τελος εγγραφη των bytes
                {                                                               //ιδια μεθοδολογια με 77
                    b=code.toByteArray();
                    writeBytes.write(b);
                    code.clear();
                    if(b.length<totalBits/8)
                        {   
                            for(int m=0;m<totalBits/8-b.length;m++)
                                writeBytes.write((byte)0);
                        }
                }
                else
                {
                    if(totalBits>=8)
                    {
                        int toWrite=totalBits/8;
                        b=new byte[toWrite];
                        leftover=cloneLeftOver(0,leftover,code,toWrite*8);
                        b=leftover.toByteArray();
                        writeBytes.write(b);
                        leftover.clear();
                        if(b.length<toWrite)
                        {  
                           for(int m=0;m<toWrite-b.length;m++)
                               writeBytes.write((byte)0);
                        }
                        code.clear(0,(toWrite*8)-1);
                        totalLO=totalBits%8;
                        leftover=cloneLeftOver(toWrite*8,leftover,code,totalBits);
                        code.clear();
                        existsLO=true;
                    }
                    else//αχρειαστη συνθηκη αλλα για αφαλεια
                    {
                        leftover=cloneLeftOver(0,leftover,code,totalBits);
                        totalLO=totalBits%8;
                        code.clear();//axreiasto
                        existsLO=true;                      
                    }  
                }
            }
        }
        if(existsLO)
        {
            b=leftover.toByteArray();
            writeBytes.write(b);
            leftover.clear();
            existsLO=false;   
        } 
        System.out.println("Compressed approximately in : "+compressedFile.length()+" from "+charCount+" bytes with LZ78...");
        writeBytes.flush();
        writeBytes.close();
        readFile.close();
    }catch (FileNotFoundException ex) 
    {
        Logger.getLogger(LZcompressing.class.getName()).log(Level.SEVERE, null, ex);
    }    
}


public static void lz78decompress() throws IOException
{
    try 
    {
        uncompressedFile=new File(path+"LZ78uncompressed.txt");
        uncompressedFile.delete();
        compressedFile=new File(path+"LZ78compressed.txt");
        uncompressedFile=new File(path+"LZ78uncompressed.txt");
        System.out.println("LZ78 TEXT DECOMPRESSING.....");
        System.out.println("Decompressing the text "+compressedFile.getName()+" in path "+compressedFile.getAbsolutePath()+" into LZ78uncompressed.txt...");
        writeFile=new BufferedWriter(new FileWriter(uncompressedFile,true));
        readBytes=new DataInputStream(new FileInputStream(compressedFile)); 
        ArrayList<String> Dictionary=new ArrayList<>();
        Dictionary.add(null);
        int index;
        int indexBits;
        existsLO=false;
        leftover=new BitSet();
        totalLO=0;
        while (readBytes.available()!=0)                                        //Ιδια λογικη,αλλα αντιστροφη,με την συμπιεση του 78,υπολογιζονται τα
        {                                                                      //απαρατιτητα bits       
            indexBits=bitCount(Dictionary.size());                              //Διαβασμα απαραιτητων bytes(πιθανοτατα με υπαρκτο leftover)           
            totalBits=indexBits+8;                                              //ελεγχος για leftover
            code=new BitSet(indexBits+8);                                       //δημιουργια των μεταβλητων και καταχωρηση στο dictionary και στο αρχειο
            if(existsLO) 
            {
                code=cloneLeftOver(0,code,leftover,totalLO);
                existsLO=false;
                totalBits-=totalLO;
                leftover.clear();  
            }
            if(totalBits%8==0)
            {
                b=new byte[totalBits/8];
                readBytes.read(b);
                code=fromByteArray(code,b,indexBits+8-totalBits,totalBits,0);
            }
            else
            {   
                b=new byte[(totalBits/8)+1];
                readBytes.read(b);
                code=fromByteArray(code,b,indexBits+8-totalBits,totalBits,0);
                leftover=fromByteArray(leftover,b,0,b.length*8,b.length*8-(b.length*8-totalBits));
                totalLO=b.length*8-totalBits;
                existsLO=true;  
            }          
            CharinInt=0;
            int totalUsed=0;
            index=setCode(indexBits,totalUsed,code);
            totalUsed+=indexBits;
            CharinInt=setCode(8,totalUsed,code);
            totalUsed+=8;
            char nextchar=(char)(CharinInt & totalNumber(8));
            code.clear();
              
            if(index==0)
            {
                Dictionary.add(String.valueOf(nextchar));
                writeFile.write(nextchar);
            }                
            else
            {
                 
                Dictionary.add(Dictionary.get(index)+nextchar);
                writeFile.write(Dictionary.get(index)+nextchar);        
            } 
            writeFile.flush();
        }
        readBytes.close();
        writeFile.close();  
    } 
    catch (FileNotFoundException ex) 
    {
        Logger.getLogger(LZcompressing.class.getName()).log(Level.SEVERE, null, ex);
    }
}


public static void main(String[] args)  throws IOException 
{  
    System.out.println("<<-------------LZ77 & LZ78 COMPRESSING METHODS--------------->>");
    System.out.println("Available texts: smallTxt.txt,mediumTxt.txt,bigTxt.txt,repeatedTxt.txt");
    System.out.println("Default text: mediumTxt.txt");
    System.out.println("Best tested compression with parameters :smallTxt.txt(31,7),mediumTxt.txt(8000,31),bigTxt(190000,15)repeatedTxt.txt(8000,6000)");
    System.out.println("WARNING:Big texts like bigTxt.txt will take some time to complete");
    System.out.println();
    System.out.println("Please give the path of a text,one of the aboves texts(with extension) or press enter for default \n(The files will be stored in the same folder).");
    System.out.println("Previous files are going to be deleted.");
    String in=read.nextLine();
    if(!in.isEmpty())
       file=new File(in); 
    if(file.getParentFile()!=null)
       path=file.getParentFile()+"\\";

    Loop: while(true)
    {   
        System.out.println("Please give the search window length for lz77 compress and decompress.");       
        srcWindowLength=read.nextInt();                      
        System.out.println("Please give the max length of the match string for lz77 compress and decompress.");   
        lookWindowLength=read.nextInt(); 
        
        System.out.println("If LZ77 compression rate is low please retry with new parameters depending on the size and repeatability of text.");
        System.out.println("If parameters are greater than the text length,then  the counter will just grow until texts length.");
        if( srcWindowLength<lookWindowLength)   
            System.err.println("the values are invalid");                 
        else                                                               
            break; 
   }    
    System.out.println();
    lz77compress();
    lz77decompress();
    lz78compress();
    lz78decompress(); 
}
}




