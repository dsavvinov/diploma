all:
	latexmk --xelatex -outdir=out -bibtex diploma.tex
	
preview: all
	evince out/diploma.pdf

clean:
		rm -rf ./out/
		