package net.mineup.mercado.database;

public enum Columns
{
    ITEM("ITEM", 0, "item"), 
    CATEGORY("CATEGORY", 1, "categoria"), 
    OWNER("OWNER", 2, "dono"), 
    PRICE("PRICE", 3, "preco"), 
    UUID("UUID", 4, "uuid"), 
    TIME("TIME", 5, "tempo"), 
    BUYER("BUYER", 6, "comprador"), 
    SEND("SEND", 7, "enviar");
    
    private String column;
    
    private Columns(final String s, final int n, final String column) {
        this.column = column;
    }
    
    public String getColumn() {
        return this.column;
    }
}
