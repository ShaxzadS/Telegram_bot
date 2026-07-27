import openpyxl, csv, sys, os
wb_path = r'C:\Users\admin\Desktop\ЗАПАД.xlsx'
if not os.path.exists(wb_path):
    print('ERROR: Excel file not found:', wb_path)
    sys.exit(1)
wb = openpyxl.load_workbook(wb_path, data_only=True)
sheet = wb.active
rows = list(sheet.iter_rows(values_only=True))
if not rows:
    print('ERROR: Excel is empty')
    sys.exit(1)
headers = [ (str(c).strip() if c is not None else '') for c in rows[0] ]
# map headers to required fields
mapping = {}
for i,h in enumerate(headers):
    lh = h.lower()
    if any(k in lh for k in ['atm','atm_number','номер']):
        mapping['atm_number']=i
    if any(k in lh for k in ['model','модель']):
        mapping['model']=i
    if any(k in lh for k in ['org','орган']):
        mapping['organization']=i
    if any(k in lh for k in ['addr','address','адрес']):
        mapping['address']=i
    if any(k in lh for k in ['sector','сектор']):
        mapping['sector']=i

print('Found headers:', headers)
print('Mapping:', mapping)
if len(mapping) < 5:
    print('ERROR: Could not map all required columns. Aborting.')
    sys.exit(2)

out_csv = r'C:\Java Spring Boot\ShaxzadBot\atm_import_zapad.csv'
with open(out_csv, 'w', newline='', encoding='utf-8') as f:
    writer = csv.writer(f)
    writer.writerow(['atm_number','model','organization','address','sector','region'])
    for row in rows[1:]:
        vals = []
        for key in ['atm_number','model','organization','address','sector']:
            idx = mapping[key]
            v = row[idx] if row and idx < len(row) else ''
            if v is None:
                v = ''
            vals.append(str(v).strip())
        vals.append('Запад')
        writer.writerow(vals)

print('WROTE CSV:', out_csv)
print('SAMPLE ROWS:')
# show first 5 rows
with open(out_csv, encoding='utf-8') as f:
    for i, line in enumerate(f):
        if i < 5:
            print(line.strip())
        else:
            break
sys.exit(0)
