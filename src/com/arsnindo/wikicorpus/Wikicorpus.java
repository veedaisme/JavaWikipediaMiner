package com.arsnindo.wikicorpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.* ;

import org.wikipedia.miner.model.Article;
import org.wikipedia.miner.model.Category;
import org.wikipedia.miner.model.Page;
import org.wikipedia.miner.model.Wikipedia;
import org.wikipedia.miner.model.Page.PageType;
import org.wikipedia.miner.util.WikipediaConfiguration;

import com.arsnindo.util.Cleaner;
import com.arsnindo.util.Connect;



public class Wikicorpus {
		
		static Wikipedia _wikipedia;
		BufferedWriter out;
		BufferedReader _input;
		Cleaner c = new Cleaner();
		Connect connect = new Connect();
		
		
public Wikicorpus(WikipediaConfiguration conf){ 
		// memuat configurasi wiki, untuk akses ke database barkeley
		_wikipedia = new Wikipedia(conf, false);
	}
	
/*
 * @param file adalah sebuah file yang berisi seluruh id artikel, category, redirect, disambigu dari wikipedia
 * @param wikipedia adalah env wikipedia yang sedang dijalankan  
 */

public void ArticleSet(File file, Wikipedia wikipedia) throws Exception{
	
	BufferedReader reader = new BufferedReader(new FileReader(file)) ;
	String line  ;

	
	while ((line = reader.readLine()) != null) {
		String[] values = line.split("\t") ;
		int id = new Integer(values[0].trim()) ;
		Article article = (Article) wikipedia.getPageById(id);
		displayMarkup(article);
		
		}
	reader.close();
}

/*
 * @param isi adalah page yang ingin dibersihkan markupnya dan tambah ke dalam file
 * 
 */

private void displayMarkup(Page isi)throws Exception {
	Cleaner markupclean = new Cleaner();
	String hasil = markupclean.getSentenceClean(isi);
	try{
	out = new BufferedWriter(new FileWriter("T:/Project2/Corpus-FULL.xml",true));

	out.append(hasil);
	out.newLine();
	out.newLine();
    out.close();
	}catch(IOException e){
		System.out.println("There was a problem:" + e);
	}
}

/*
 * @param p adalah page yang akan diambil title dan id nya yang kemudian dimasukan kedalam file
 * 
 */

protected void deskrip(Page p) throws Exception {
	String title = p.getTitle();
	int id = p.getId();
	
	try{
		out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/templateById.tsv",true));
		out.append(String.format("%-15s %-15s %n", id, title));
		out.close();
		System.out.println("Artikel dengan " +id+ " telah di deskrip");
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}
}


private void deskriptor() throws Exception{
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		deskrip(p);	
	}
}


/*
 * method untuk melakukan iterasi terhadap artikel yang ada kemudian di bersihkan (membuat corpus)
 */


private void corpusClean()  throws Exception {
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		displayMarkup(p);	
	}
}

/*
 * method bantuan untuk memabersihkan artikel dan mengambil box nya
 */

private void box(Page p) throws Exception{
	Cleaner c = new Cleaner();
	String hasil = c.getBox(p);
	
	int id = p.getId();
	
	/*
	 * tidak semua artikel memiliki infobox
	 */
	if (hasil == ""){
		
		try{
			out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/box/nobox/list.nobox.txt", true));
			out.append(String.format("%-15s %-15s %n", p.getId(), p.getTitle()));
			out.newLine();
			out.close();
			System.out.println( id+ " tidak mempunyai box");
		}catch (IOException e){
			System.out.println("There was a problem:" + e);
		}
		
	}else{
		
		try{
			out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/box/"+ id + ".box.txt"));
			out.write(hasil);
			out.newLine();
			out.close();
			System.out.println("Artikel dengan id " +id+ " telah di diextract boxnya");
			}catch(IOException e){
				System.out.println("There was a problem:" + e);
			}
	
	}
}


/*
 * method untuk mengambil info box dari wikipedia
 */

private void boxer() throws Exception {
	Iterator<Page> iter = _wikipedia.getPageIterator(PageType.article); 
	Page p ;
	while(iter.hasNext()){
		Page p1 = iter.next();
		p=p1;
		box(p);	
	}
}

private void doc(Page p) throws IOException{
	Cleaner c = new Cleaner();
	String hasil = c.getDesc(p);
	int id = p.getId();
	
	out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/document/"+ id + ".doc.txt"));
	out.write(hasil);
	out.newLine();
	out.close();
	System.out.println("Artikel dengan id " +id+ " telah di diextract boxnya");
	
}

private void docer() throws IOException {
	 Page article = _wikipedia.getPageById(44);
	 doc(article);
	 
//     System.out.println(article.getType());
//     System.out.println(article.getTitle());
//     System.out.println(article.getMarkup());
//     System.out.println(article.getFirstParagraphMarkup());
	
}

private String ambilKategori(String ask, String p, int id) throws IOException{
	Page kategori = _wikipedia.getCategoryByTitle(p);
	String judul = null;
	String query = ask;
	String hasil = null;
	Cleaner c = new Cleaner();
	
	
	if(kategori != null){
		judul = kategori.getTitle();
		if(c.isKategori(judul, query)){
			hasil = judul;
		}else
			hasil = null;
		}
	else
		hasil = null;
	
	return hasil;
}

private Integer getInt(String prompt, int min, int max) throws IOException {
	
	while (true){
		
		System.out.println(prompt + " (" + min + " - " + max + " atau ENTER untuk mengkosongkan)");
		
		String line = _input.readLine();
		if (line.trim().equals(""))
			return null;
		
		try{
			Integer val = Integer .parseInt(line);
			if (val >= min && val <= max)
                return val ;
		}catch (Exception e){
			
		}
		
		System.out.println("Input tidak tepat, silakan mencoba lagi");
				
	}
			
}

private String getString(String prompt) throws IOException{
	System.out.println(prompt + "(atau ENTER untuk mengosongkan)");
	String line = _input.readLine();
	if (line.trim().equals(""))
		return null;
	return line;
}

/*
 * delay 
 * @param n time in miliseconds
 */

private void delay(long n){
	try{
	Thread.sleep(n);
	}catch (InterruptedException e){
		
	}
}

// get category without matching query

private String ambilKategoriWOQuery(String p) throws IOException{
	Page kategori = _wikipedia.getCategoryByTitle(p);
	String judul = null;
	String hasil = null;
	
	
	if(kategori != null){
		judul = kategori.getTitle();
		hasil = judul;
		}
	else
		hasil = null;
	
	return hasil;
}

/*
 * insert all (only-content), (only-box), (only-category) to DATABASE
 */

public void inputToDatabase(){
	_input = new BufferedReader(new InputStreamReader(System.in));
	Page p;
	List<String> kategoriTerkait = new ArrayList<String>();
	connect.connect();
	

		int k = 0;
		Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
		System.out.println("\nMohon tunggu~\n");
			for (k=0; k<66889; k++){
				Page p1 = iter.next();
				p=p1;
				try{
				String isi = ambilKategoriWOQuery(p.getTitle());
					if (isi != null){
					kategoriTerkait.add(isi);
					if(k == 66889)
						((BufferedReader) iter).close();
				}
				}catch(IOException e){
					System.err.println("Terjadi masalah ketika loop kategory");
					
				}
			}
			

			for (int i=0 ; i<kategoriTerkait.size() ; i++) {
				System.out.println("** Kategori '" +  kategoriTerkait.get(i) + "' - eksraksi dimulai");
				
				Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(i));
				Category[] listChild = kategori.getChildCategories();
				Article[] listArtikel = kategori.getChildArticles();
				String hasil = "";
				
				/*
				 * to handle if some category have child category
				 */
				if (listChild.length != 0){
					for (int l=0 ; l<listChild.length ; l++) {
						kategoriTerkait.add(listChild[l].getTitle());
					}
				}
				
				for (int j=0 ; j<listArtikel.length ; j++) { 
					System.out.println("	- "+listArtikel[j].getId()+"- Artikel '" +  listArtikel[j].getTitle() + "' - DIEKSTRAK");
					hasil = c.getDesc(listArtikel[j]);
					
					//MENGAMBIL KONTEN ARTIKEL//
					
					connect.createDoc(listArtikel[j].getId(), listArtikel[j].getTitle(), hasil);
					connect.createCat(kategoriTerkait.get(i), listArtikel[j].getId());
					
		           }
				System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - ekstraksi selesai");
				delay(500);
			}
			
			kategoriTerkait.clear();
	}


public void getArticleByCategory() throws Exception{
	_input = new BufferedReader(new InputStreamReader(System.in));
	Page p;
	String termKategori;
	Page penampung;
	List<String> kategoriTerkait = new ArrayList<String>(); 
	
	while((termKategori = getString("Silakan masukan kategori yang anda mau cari")) != null){
		int k = 0;
		Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
		System.out.println("\nMohon tunggu~\n");
			for (k=0; k<66889; k++){
				Page p1 = iter.next();
				p=p1;
				String isi = ambilKategori(termKategori, p.getTitle(), p.getId());
				if (isi != null){
				kategoriTerkait.add(isi);
				if(k == 66889)
					((BufferedReader) iter).close();
				}
			}
			

			if(kategoriTerkait.size() == 0){
					System.out.println("\n== Mohon maaf kata kunci untuk kategori yang anda cari tidak ada ==\n");
			}else{
				System.out.println("== term kategori " + termKategori + " ada di dalam beberapa kategori yaitu :");
				
				for (int i=0 ; i<kategoriTerkait.size() ; i++) {
		            System.out.println(" - [" + (i+1) + "] " + kategoriTerkait.get(i));
		        }	
				
				Integer kategoriIndex = getInt("Pilih Kategori yang anda cari", 1, kategoriTerkait.size());
//				try{
				Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(kategoriIndex-1));
				Article[] listArtikel = kategori.getChildArticles();
				
				
				if (listArtikel.length == 0) {
			    	   System.out.println("\n!! Kategori '" + kategoriTerkait.get(kategoriIndex-1) + "' tidak mempunyai turunan artikel !!");
			       }else {
			    	   System.out.println("\n=== Turunan artikel untuk Kategori '" + kategoriTerkait.get(kategoriIndex-1) + "' yaitu : ===");
			           for (int i=0 ; i<listArtikel.length ; i++) { 
			               System.out.println(" - [" + (i+1) + "] " + listArtikel[i].getTitle()) ;
			           }
			           Integer artikelIndex = getInt("\nPilih artikel yang anda cari", 1, listArtikel.length);
			           penampung = _wikipedia.getArticleByTitle(listArtikel[artikelIndex-1].getTitle());
					   System.out.println("\n" + penampung.getMarkup()+ "\n");
			           
			       }
			}
			
			kategoriTerkait.clear();
	}
}


/*
 * @param id adalah id dari artikel yang ingin dilihat kategorinya
 * 
 */

public void getCategoryParentChild(int id){
	
	Page article = _wikipedia.getPageById(id);
//    System.out.println(article.getType());
//    System.out.println(article.getTitle());
    
    
    /// WORKSPACE ///
    
    Category kategori = _wikipedia.getCategoryByTitle(article.getTitle());
    
    if(kategori != null){
    	
    	System.out.println(kategori.getId());
    	System.out.println(kategori.getTitle());
    }
    
   Category[] tempChild  = kategori.getChildCategories();
   Category[] tempParent = kategori.getParentCategories();
   
   if (tempChild.length == 0) {
	   System.out.println("=== Kategori ini tidak mempunyai turunan kategori ===");
   } else {
	   System.out.println("=== Kategori Turunan untuk Kategori ini yaitu : ===");
       for (int i=0 ; i<tempChild.length ; i++) { 
           System.out.println(" - [" + (i+1) + "] " + tempChild[i].getTitle()) ;
       }
       
   }
   
   System.out.println("\n") ;
   
   if (tempParent.length == 0) {
	   System.out.println("=== Kategori ini tidak mempunyai parent kategori ===");
   } else {
	   System.out.println("=== Kategori parent untuk Kategori ini yaitu : ===");
       for (int i=0 ; i<tempParent.length ; i++) {
    	   
           System.out.println(" - [" + (i+1) + "] " + tempParent[i].getTitle()) ;
       }
       
   }
}

	public void getDescFromArticle() throws Exception{
		_input = new BufferedReader(new InputStreamReader(System.in));
		Page p;
		String termKategori;
		Page penampung;
		List<String> kategoriTerkait = new ArrayList<String>(); 
		int counter = 0;
		
		while((termKategori = getString("Silakan masukan kategori yang anda mau cari")) != null){
			int k = 0;
			Iterator<Page> iter = _wikipedia.getPageIterator(PageType.category);
			System.out.println("\nMohon tunggu~\n");
				for (k=0; k<66889; k++){
					Page p1 = iter.next();
					p=p1;
					String isi = ambilKategori(termKategori, p.getTitle(), p.getId());
					if (isi != null){
					kategoriTerkait.add(isi);
					if(k == 66889)
						((BufferedReader) iter).close();
					}
				}
				
				for (int i=0 ; i<kategoriTerkait.size() ; i++) {
					System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - eksraksi dimulai");
					
					Category kategori = _wikipedia.getCategoryByTitle(kategoriTerkait.get(i));
					Category[] listChild = kategori.getChildCategories();
					Article[] listArtikel = kategori.getChildArticles();
					String hasil = "";
					
					/*
					 * to handle if soe
					 */
					if (listChild.length != 0){
						for (int l=0 ; l<listChild.length ; l++) {
							kategoriTerkait.add(listChild[l].getTitle());
						}
					}
					
					for (int j=0 ; j<listArtikel.length ; j++) { 
						System.out.println("	- Artikel '" +  listArtikel[j].getTitle() + "' - ekstraksi dimulai");
						hasil = c.getDesc(listArtikel[j]);
						
						//MENGAMBIL KONTEN ARTIKEL//
						
						out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/doc/"+ listArtikel[j].getId() + ".doc.txt"));
						out.write(hasil);
						out.newLine();
						out.close();
						
						//------------------------//
						
						//MENGAMBIL BOX ARTIKEL   //
						
//						hasil = c.getBox(listArtikel[j]);
//						if(hasil != ""){
//							try{	
//							out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/box/"+ listArtikel[j].getId() + ".box.txt"));
//							out.write(hasil);
//							out.newLine();
//							out.close();
//							}catch(NullPointerException e){
//								System.err.println(listArtikel[j].getTitle() + " dan " +listArtikel[j].getId()+ " Tidak mempunyai markup");
//							}
//						}else{
//							out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/box/nobox/NoBox.txt", true));
//							out.append(String.format("%-15s %-15s %-15s %-15s %n", counter, kategoriTerkait.get(i), listArtikel[j].getId() , listArtikel[j].getTitle()));
//							out.newLine();
//							out.close();
//						}
						
						//-------------------------//
						
						//MENGAMBIL LIST ARTIKEL//
//						counter++ ;
//						out = new BufferedWriter(new FileWriter("X:/Database Berkeley/Output/dalam tahun 2015/doc/test/listArtikel.txt", true));
//						out.append(String.format("%-15s %-15s %-15s %-15s %n", counter, kategoriTerkait.get(i), listArtikel[j].getId() , listArtikel[j].getTitle()));
//						out.newLine();
//						out.close();
						
						//-------------------------//
						
						System.out.println("	-Artikel dengan id " +listArtikel[j].getTitle()+ " telah di diextract boxnya");
			           }
					System.out.println("* Kategori '" +  kategoriTerkait.get(i) + "' - ekstraksi selesai");
		        }
				
				kategoriTerkait.clear();
		}
	}
	

    public static void main(String args[]) throws Exception {
    	WikipediaConfiguration conf = new WikipediaConfiguration(new File("X:/Database Berkeley/wikipedia-miner-1.2.0/wikipedia-template.xml"));
    	Wikicorpus korpus = new Wikicorpus(conf);
    	//korpus.ArticleSet((new File("X:/Database Berkeley/datadir/pageId.csv")), _wikipedia);
    	
    	//korpus.docer();
    	
    	//korpus.deskriptor();
    	
    	//korpus.getArticleByCategory();
    	
    	korpus.inputToDatabase();
    	
    	//korpus.getDescFromArticle();
    	/*
    	 * membuat corpus dari seluruh artikel wikipedia
    	 */
    	//korpus.corpusClean();
    	
    }

   

	
}