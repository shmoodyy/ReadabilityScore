
class Problem {
    public static void main(String[] args) {
        String regex = "\\btest\\b";
        int index = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].matches(regex)) {
                index = i;
            }
        }
        System.out.println(index);
    }
}