char *foo()
{
    char str[10];

    // array index out of bounds (the trailing zero is written at str[10])
    strcpy(str, "1234567890");

    // Array index out of bounds.. array[0xffffffffff]
    int array[256];
    char a = 0xff;
    array[a] = 0;

    // array index out of bounds
    for (unsigned int i = 0; i <= sizeof(str); i++)
        str[i] = 0;

    // unsigned division error, the result will not be -10
    unsigned int ten = 10;
    int result = -100 / ten;

    // unusual pointer arithmetic.. the abcde string will not get the value "abcde"
    std::string abcde = "abcd" + 'e';

    // sprintf: overlapping data used in input/output. the result is undefined
    sprintf(str, " %s", str);

    // mismatching allocation size..
    int *p = malloc(25);
    free(p);

    // mismatching allocation and deallocation..
    char *str2 = new char[100];
    delete str2;

    // resource leak..
    FILE *f = fopen("foo.txt", "wt");
    // no fclose

    std::list<unsigned int> ints1;
    std::list<unsigned int> ints2;
    ints1.push_back(1);
    ints1.push_back(2);
    ints1.push_back(3);
    // iterator loop problem.. the "ints2.end()" has a typo
    for (std::list<unsigned int>::iterator it = ints1.begin(); it != ints2.end(); it++)
    {
        /* ... */
    }

    // Returning pointer to local stack variable
    return str;
}